package ch.fhnw.sna.twitter;

import ch.fhnw.sna.twitter.model.HumanTwitterUser;
import ch.fhnw.sna.twitter.model.NewsportalGraph;
import ch.fhnw.sna.twitter.model.NewsportalTwitterUser;
import twitter4j.*;

import java.util.ArrayList;

public class NewsportalFetcher {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NewsportalFetcher.class);
    private Twitter twitter = new TwitterFactory().getInstance();

    public NewsportalGraph fetch(ArrayList<String> newsportals) {
        NewsportalGraph graph = new NewsportalGraph();

        try {
            fetchNewsportals(newsportals, graph);
            fetchHumans(newsportals, graph);
        } catch (TwitterException e) {
            LOG.error("Got TwitterException while fetching: {}", e.getErrorMessage());
        }


        return graph;
    }

    public void fetchNewsportals(ArrayList<String> newsportals, NewsportalGraph graph) throws TwitterException {
        for (String newsportal : newsportals) {
            
            NewsportalTwitterUser tu = new NewsportalTwitterUser(twitter.showUser(newsportal)); 
            graph.addNewsportal(tu);
            
            LOG.info("Fetching newsportal "+newsportal+" ("+tu.getFollowersCount()+")");
        }
    }

    public void fetchHumans(ArrayList<String> newsportals, NewsportalGraph graph) throws TwitterException {

        long cursor = -1;
        IDs ids;
        Twitter twitter = new TwitterFactory().getInstance();
        do {
            ids = twitter.getFollowersIDs("tagi", cursor, -1);

            LOG.info("Fetched ids " + ids.getIDs().length);
            /*for (long id : ids.getIDs()) {
                System.out.println("fetched id count " + id);
            }*/
        } while ((cursor = ids.getNextCursor()) != 0);

    }
}
