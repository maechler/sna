package ch.fhnw.sna.twitter;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;

import ch.fhnw.sna.twitter.database.DatabaseAccess;
import ch.fhnw.sna.twitter.model.NewsportalGraph;
import com.almworks.sqlite4java.SQLiteException;
import twitter4j.TwitterException;

public class NewsportalFetcher_Main {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NewsportalFetcher_Main.class);

    public static void main(String[] args) throws TwitterException, InterruptedException, IOException, SQLiteException, ParseException {
        if (args.length == 0) {
            LOG.error("Must provide an argument: fetch, export");

            return;
        }

        ArrayList<String> newsportals = new ArrayList<String>() {};
        newsportals.add("FHNWTechnik");
        newsportals.add("fhnw_i4ds");
        newsportals.add("IT_FHNW");
        newsportals.add("iwifhnw");
        newsportals.add("ic_fhnw");
        newsportals.add("dotFHNW");
        newsportals.add("ITHGKFHNW");

        switch (args[0]) {
            case "fetch":
                if (args.length < 2) {
                    LOG.error("Must specify two arguments for command fetch: fetch ids, fetch newsportals, fetch newsportalFollowers, fetch humanFollowers");
                } else {
                    switch (args[1]) {
                        case "ids":
                            fetchIds(args, newsportals);
                            break;
                        case "newsportals":
                            fetchNewsportals(args, newsportals);
                            break;
                        case "newsportalFollowers":
                            fetchNewsportalFollowers(args, newsportals);
                            break;
                        case "humanFollowers":
                            fetchHumanFollowers(args);
                            break;
                        default:
                            LOG.error("Invalid command: fetch " + args[1]);
                    }
                }
                break;
            case "export":
                export(args);
                break;
                
            case "analyse":
                switch (args[1]) {
                    case "UserOverlapsAllNewsportals":
                        LOG.info("Method finished: analyseUserOverlapsAllNewsportals");
                        int[] list = new AnalyseFollowerOverlaps().analyseUserOverlapsAllNewsportals(newsportals);

                        int i = 0;
                        for(int s : list)
                        {
                            System.out.println("Followers with connection to "+i+" of "+newsportals.size()+": "+s);
                            i++;
                        }


                        LOG.info("Method finished: analyseUserOverlapsAllNewsportals");
                        break;
                    case "UserOverlapsAllNewsportalsID":
                        LOG.info("Method finished: analyseUserOverlapsAllNewsportalsID");
                        String[][] IDlist = new AnalyseFollowerOverlaps().analyseUserOverlapsAllNewsportalsID(newsportals);

                        for (int j = newsportals.size(); j>0; --j){
                            System.out.println("");
                            System.out.println("ID's of followers with connection to "+j+" of "+newsportals.size()+": ");

                            int count = 0;

                            for (int k=0; k<IDlist[0].length; ++k ){
                                if (IDlist[j][k] != null) {
                                    System.out.println(IDlist[j][k]);
                                    count++;
                                }
                            }
                            System.out.println("");
                            System.out.println("Total followers with connection to "+j+" of "+newsportals.size()+": " + count);
                            System.out.println("");
                        }

                        LOG.info("Method finished: analyseUserOverlapsAllNewsportalsID");
                        break;
                    case "SpecificOverlaps":
                        analyseSpecificOverlaps();
                        break;
                    case "SpecificOverlaps5percent":
                        analyseSpecificOverlaps5percent();
                        break;
                }
            break;
                
            default:
                LOG.error("Invalid command provided: " + args[0]);
        }
    }

    private static void fetchIds(String[] args, ArrayList<String> newsportals) throws SQLiteException, IOException, TwitterException, InterruptedException {
        LOG.info("Method started: fetch ids");

        DatabaseAccess db = new DatabaseAccess();
        NewsportalFetcher newsportalFetcher = new NewsportalFetcher(db);

        newsportalFetcher.fetchNewsportalFollowerIDsToFile(newsportals);
        newsportalFetcher.writeRandomIDsFromNewsportalFollowerIDsToFile(newsportals);

        db.close();

        LOG.info("Method finished: fetch ids");
    }

    private static void fetchHumanFollowers(String[] args) throws SQLiteException, IOException, TwitterException, InterruptedException, ParseException {
        LOG.info("Method started: fetch humanFollowers");

        DatabaseAccess db = new DatabaseAccess();
        NewsportalFetcher newsportalFetcher = new NewsportalFetcher(db);

        newsportalFetcher.fetchHumanFollowers();
        db.close();

        LOG.info("Method finished: fetch humanFollowers");
    }

    private static void fetchNewsportalFollowers(String[] args, ArrayList<String> newsportals) throws SQLiteException, IOException, TwitterException, InterruptedException, ParseException {
        LOG.info("Method started: fetch newsportalFollowers");

        DatabaseAccess db = new DatabaseAccess();
        NewsportalFetcher newsportalFetcher = new NewsportalFetcher(db);

        newsportalFetcher.fetchHumans(newsportals);
        db.close();

        LOG.info("Method finished: fetch newsportalFollowers");
    }

    private static void fetchNewsportals(String[] args, ArrayList<String> newsportals) throws SQLiteException, IOException, TwitterException {
        LOG.info("Method started: fetch newsportals");

        DatabaseAccess db = new DatabaseAccess();
        NewsportalFetcher newsportalFetcher = new NewsportalFetcher(db);

        newsportalFetcher.fetchNewsportals(newsportals);
        db.close();

        LOG.info("Method finished: fetch newsportals");
    }

    private static void export(String[] args) throws SQLiteException, IOException, ParseException {
        LOG.info("Method started: export");
        String filename = "Newsportals.gexf";
        DatabaseAccess db = new DatabaseAccess();

        if (args.length < 2) {
            LOG.info("No filename specified, using default " + filename);
        } else {
            filename = args[1];
        }

        new NewsportalGephiExport(filename).export(NewsportalGraph.createFromDB(db));
        db.close();

        LOG.info("Method finished: export");
    }
    
    private static void analyseSpecificOverlaps() throws IOException{
        LOG.info("Method started: analyseSpecificOverlaps");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("FHNWTechnik","fhnw_i4ds");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("FHNWTechnik","IT_FHNW");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("FHNWTechnik","iwifhnw");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("FHNWTechnik","ic_fhnw");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("FHNWTechnik","dotFHNW");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("FHNWTechnik","ITHGKFHNW");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("fhnw_i4ds","IT_FHNW");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("fhnw_i4ds","iwifhnw");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("fhnw_i4ds","ic_fhnw");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("fhnw_i4ds","dotFHNW");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("fhnw_i4ds","ITHGKFHNW");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("IT_FHNW","iwifhnw");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("IT_FHNW","ic_fhnw");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("IT_FHNW","dotFHNW");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("IT_FHNW","ITHGKFHNW");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("iwifhnw","ic_fhnw");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("iwifhnw","dotFHNW");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("iwifhnw","ITHGKFHNW");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("ic_fhnw","dotFHNW");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("ic_fhnw","ITHGKFHNW");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("dotFHNW","ITHGKFHNW");
        LOG.info("Method finished: analyseSpecificOverlaps");
    }
    
    private static void analyseSpecificOverlaps5percent() throws IOException{
        LOG.info("Method started: analyseSpecificOverlaps5percent");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","watson_news");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","Weltwoche");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","blickamabend");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","Blickch");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","NZZ");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","20min");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","Weltwoche");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","blickamabend");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","Blickch");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","NZZ");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","20min");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Weltwoche","blickamabend");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Weltwoche","Blickch");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Weltwoche","NZZ");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Weltwoche","20min");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("blickamabend","Blickch");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("blickamabend","NZZ");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("blickamabend","20min");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Blickch","NZZ");
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Blickch","20min");
        
        new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("NZZ","20min");
        LOG.info("Method finished: analyseSpecificOverlaps5percent");
    }
}
