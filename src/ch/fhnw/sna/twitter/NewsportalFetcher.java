package ch.fhnw.sna.twitter;

import ch.fhnw.sna.twitter.database.DatabaseAccess;
import ch.fhnw.sna.twitter.model.NewsportalGraph;
import ch.fhnw.sna.twitter.model.TwitterUser;
import com.almworks.sqlite4java.SQLiteException;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.*;
import java.util.List;

public class NewsportalFetcher {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NewsportalFetcher.class);
    private List<Twitter> twitters = new ArrayList();
    private int currentTwitterIndex = 0;
    private Twitter twitter = new TwitterFactory().getInstance();
    private static final int ERROR_CODE_RATE_LIMIT_EXCEEDED = 88;
    private static final int STATUS_CODE_ACCESS_DENIED = 401;
    private DatabaseAccess db;
    private boolean useFastFarming = false;

    public NewsportalFetcher(DatabaseAccess db) {
        this.db = db;

        if (useFastFarming) {
            ConfigurationBuilder cb = new ConfigurationBuilder();
            cb.setDebugEnabled(true)
                    .setOAuthConsumerKey("*")
                    .setOAuthConsumerSecret("*")
                    .setOAuthAccessToken("*")
                    .setOAuthAccessTokenSecret("*");
            TwitterFactory tf = new TwitterFactory(cb.build());
            Twitter twitter = tf.getInstance();

            ConfigurationBuilder cb2 = new ConfigurationBuilder();
            cb2.setDebugEnabled(true)
                    .setOAuthConsumerKey("*")
                    .setOAuthConsumerSecret("*")
                    .setOAuthAccessToken("*")
                    .setOAuthAccessTokenSecret("*");
            TwitterFactory tf2 = new TwitterFactory(cb2.build());
            Twitter twitter2 = tf2.getInstance();

            twitters.add(twitter);
            twitters.add(twitter2);
        } else {
            twitters.add(this.twitter);
        }
    }

    public void fetchHumanFollowers() throws TwitterException, SQLiteException, InterruptedException, ParseException {
        List<Long> nodeIds = db.findAllNodeIds();
        int requestLimitPerMinutes = 15; //request limit per 15 minutes
        long cursor;
        IDs ids;
        double total = nodeIds.size();
        double current = 0;
        boolean foundSpecialUser;
        int friendsCount = 0;
        DecimalFormat df = new DecimalFormat("#0.00");
        List<Long> specialUsers = new ArrayList<>();

        for (Long id : nodeIds) {
            current++;
            //if (db.countEdgesFrom(id) < 2) continue;
            if (specialUsers.contains(id)) continue;

            TwitterUser node = db.findNodeById(id);
            cursor =-1L;
            ids = null;
            foundSpecialUser = false;

            if (!node.getLoadedFollowers() && node.getFollowersCount() < 100000) {
                do {
                    try {
                        ids = twitters.get(currentTwitterIndex).getFollowersIDs(id, cursor);

                    } catch (TwitterException e) {
                        if (e.getStatusCode() == STATUS_CODE_ACCESS_DENIED) {
                            specialUsers.add(id);
                            foundSpecialUser = true;

                            LOG.info("Special user: " + node.getScreenName() + " (" + specialUsers.size() + ")");
                        } else if (e.getErrorCode() == ERROR_CODE_RATE_LIMIT_EXCEEDED) {
                            if (currentTwitterIndex + 1 == twitters.size()) {
                                LOG.info("TwitterException:  " + e.getErrorMessage());
                                LOG.info("Sleeping for  " + ((requestLimitPerMinutes / twitters.size()) + 1) + "min");
                                LOG.info("Followers loaded to " + df.format((current / total)*100) + "%");
                                LOG.info("Newly Found friends: " + friendsCount);

                                Thread.sleep(((requestLimitPerMinutes / twitters.size()) + 1)*60*1000);
                                currentTwitterIndex = 0;
                                LOG.info("Resetting twitter to index=" + currentTwitterIndex);
                            } else {
                                currentTwitterIndex++;
                                LOG.info("Switching twitter to index=" + currentTwitterIndex);
                            }
                        } else {
                            throw e;
                        }
                    }

                    if (ids != null) {
                        for (long fromId : ids.getIDs()) {
                            if (nodeIds.contains(fromId)) {
                                friendsCount++;
                                db.saveEdge(fromId, node.getId());
                            }
                        }
                    }
                } while((ids == null || (cursor = ids.getNextCursor())!=0) && !foundSpecialUser);

                if (!foundSpecialUser) {
                    node.setLoadedFollowers(true);
                    db.saveNode(node);
                }
            }
        }

        LOG.info(specialUsers.size() + " special users found." );

        for (Long specialUserId : specialUsers) {
            TwitterUser node = db.findNodeById(specialUserId);
            LOG.info("special user: " + node.getScreenName());
        }
    }

    public void fetchNewsportals(ArrayList<String> newsportals) throws TwitterException, SQLiteException {
        for (String newsportal : newsportals) {
            db.saveNode(new TwitterUser(twitter.showUser(newsportal), TwitterUser.TYPE_NEWSPORTAL));
            
            LOG.info("Fetched newsportal " + newsportal);
        }
    }

    public void fetchHumans(ArrayList<String> newsportals) throws InterruptedException, IOException, TwitterException, SQLiteException, ParseException {
        int requestLimitPerMinutes = 15; //request limit per 15 minutes
        int rowsPerRequest = 100;
        List<String> newsportalIds;
        long[] newsportalIdsForRequest;
        DecimalFormat df = new DecimalFormat("0.00");

        for (String newsportal : newsportals) {
            newsportalIds = readIds(newsportal);
            TwitterUser newsportalObject = db.findNodeByScreenName(newsportal);

            if (newsportalObject.getLoadedFollowers()) {
                LOG.info("Users already fetched for newsportal " + newsportal);

                continue;
            }

            for (int i = 0; i < newsportalIds.size();) {
                newsportalIdsForRequest = new long[rowsPerRequest];

                for (int j=0; j<rowsPerRequest && i < newsportalIds.size(); j++) {
                    newsportalIdsForRequest[j] = Long.parseLong(newsportalIds.get(i));

                    i++;
                }

                try {
                    ResponseList<User> users = twitter.lookupUsers(newsportalIdsForRequest);

                    for (User user : users) {
                        TwitterUser twitterUser = new TwitterUser(user, TwitterUser.TYPE_HUMAN);
                        db.saveNode(twitterUser);
                        db.saveEdge(twitterUser.getId(), newsportalObject.getId());
                    }
                } catch (TwitterException e) {
                    if (e.getErrorCode() != ERROR_CODE_RATE_LIMIT_EXCEEDED) throw e;

                    LOG.info("TwitterException:  " + e.getErrorMessage());
                    LOG.info("Sleeping for  " + (requestLimitPerMinutes + 1) + "min");
                    LOG.info("Followers for " + newsportalObject.getScreenName() +" loaded to " + df.format((((double) i) / ((double) newsportalIds.size()))*100) + "%");

                    Thread.sleep((requestLimitPerMinutes + 1) * 60 * 1000);
                }
            }

            newsportalObject.setLoadedFollowers(true);
            db.saveNode(newsportalObject);

            LOG.info("Fetched users for newsportal " + newsportalObject.getScreenName() + " (" + newsportalIds.size() + ")");
        }

        LOG.info("Fetched all users for every newsportal");
    }

    private List<String> readIds(String newsportal) throws IOException {
        BufferedReader in = new BufferedReader(new FileReader("data/"+newsportal+".txt"));
        List<String> ids = new ArrayList<>();

        for (String line = in.readLine(); line != null; line = in.readLine()) {
            ids.add(line);
        }

        in.close();

        return ids;
    }

    public void fetchNewsportalFollowerIDsToFile(ArrayList<String> newsportals) throws TwitterException, InterruptedException, IOException {
        for(String newsportal : newsportals)
        {
            File f = new File("data/"+newsportal+".txt");
            f.createNewFile();
            
            FileWriter writer = new FileWriter("data/"+newsportal+".txt", false);
            
            long cursor = -1;
            IDs ids = null;
            IDs ids2 = null;
            Twitter twitter = new TwitterFactory().getInstance();
            do {
                try
                {
                    writer = new FileWriter("data/"+newsportal+".txt", true);
                    ids2 = ids;
                    ids = twitter.getFollowersIDs(newsportal, cursor, -1);
                    LOG.info("Fetched ids " + ids.getIDs().length + " from "+newsportal);
                    int i = 0;
                    for(i = 0;i<ids.getIDs().length;++i)
                    {
                        writer.write(Objects.toString(ids.getIDs()[i])+"\r\n");
                    }
                    
                    writer.close();
                    LOG.info(i+" ids written to /data/" + newsportal + ".txt");
                }
                catch(TwitterException e)
                {
                    ids = ids2;
                    LOG.info(e.toString());
                    LOG.info("Sleeping...");
                    Thread.sleep(16*60*1000);
                }
                finally
                {
                    writer.close();
                }
            } while ((cursor = ids.getNextCursor()) != 0);  
            
            // Nach jedem Newsportal 16 Minuten warten
            //LOG.info("Sleeping...");
            //Thread.sleep(16 * 60 * 1000);
        }
        LOG.info("End");
    }

    public void writeRandomIDsFromNewsportalFollowerIDsToFile(ArrayList<String> newsportals) throws IOException
    {
        for(String newsportal : newsportals)
        {
            // ID Datei lesen und in Array speichern
            FileReader fileReader = new FileReader("data/"+newsportal+".txt");

            BufferedReader bufferedReader = new BufferedReader(fileReader);
            ArrayList<String> lines = new ArrayList<String>();
            String line = null;
            while ((line = bufferedReader.readLine()) != null)
            {
                lines.add(line);
            }
            bufferedReader.close();
           
            // Zufallszahlengenerator welche 5% der Abzahl IDs in einer eindeutigen Liste zur�ckgibt
            Random rng = new Random(); // Ideally just create one instance globally
            Set<Integer> generated = new LinkedHashSet<Integer>();
            while (generated.size() < (lines.size()*0.05))
            {
                Integer next = rng.nextInt(lines.size()) + 1;
                generated.add(next);
            }
         
            // Neue Datei mit den zuf�llig gew�hlen 5% schreiben
            File f = new File("data/"+newsportal+"_5percent.txt");
            f.createNewFile();
            
            FileWriter writer = new FileWriter("data/"+newsportal+"_5percent.txt", false);
            
            for(int number : generated)
            {
                writer = new FileWriter("data/"+newsportal+"_5percent.txt", true);
                writer.write(lines.get(number)+"\r\n");
                writer.close();
            }
        }
    }
    
    public NewsportalGraph fetchUserInfosToGraph(ArrayList<String> newsportals) throws InterruptedException, IOException {
        NewsportalGraph graph = new NewsportalGraph();

        try {
            fetchNewsportalInfoToGraph(newsportals, graph);
            fetchHumanInfoToGraph(newsportals, graph);
        } catch (TwitterException e) {
            LOG.error("Got TwitterException while fetching: {}", e.getErrorMessage());
        }

        return graph;
    }

    private void fetchNewsportalInfoToGraph(ArrayList<String> newsportals, NewsportalGraph graph) throws TwitterException {
        for (String newsportal : newsportals) {
            graph.addNode(new TwitterUser(twitter.showUser(newsportal), TwitterUser.TYPE_NEWSPORTAL));
            
            LOG.info("Fetched newsportal " + newsportal);
        }
    }

    private void fetchHumanInfoToGraph(ArrayList<String> newsportals, NewsportalGraph graph) throws InterruptedException, IOException, TwitterException {
        int requestLimitPerMinutes = 15; //request limit per 15 minutes
        int rowsPerRequest = 100;
        List<String> newsportalIds;
        long[] newsportalIdsForRequest;

        for (String newsportal : newsportals) {
            newsportalIds = readIds(newsportal);

            for (int i = 0; i < newsportalIds.size();) {
                newsportalIdsForRequest = new long[rowsPerRequest];

                for (int j=0; j<rowsPerRequest && i < newsportalIds.size(); j++) {
                    newsportalIdsForRequest[j] = Long.parseLong(newsportalIds.get(i));

                    i++;
                }

                try {
                    ResponseList<User> users = twitter.lookupUsers(newsportalIdsForRequest);

                    for (User user : users) {
                        graph.addNode(new TwitterUser(user, TwitterUser.TYPE_HUMAN));
                    }
                } catch (TwitterException e) {
                    if (e.getErrorCode() != ERROR_CODE_RATE_LIMIT_EXCEEDED) throw e;

                    LOG.info("TwitterException:  " + e.getErrorMessage());
                    LOG.info("Sleeping for  " + (requestLimitPerMinutes + 1) + "min");

                    Thread.sleep((requestLimitPerMinutes + 1)*60*1000);
                }
            }

            LOG.info("Fetched users for newsportal " + newsportal + " (" + newsportalIds.size() + ")");
        }

        LOG.info("Fetched all users for every newsportal");
    }
}
