package ch.fhnw.sna.twitter;

import ch.fhnw.sna.twitter.model.NewsportalGraph;
import twitter4j.*;

import java.util.ArrayList;

public class NewsportalFetcher {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NewsportalFetcher.class);

    public NewsportalGraph fetch(ArrayList<String> newsportals) {
        NewsportalGraph graph = new NewsportalGraph();

        try {
            long cursor = -1;
            PagableResponseList<User> followers;
            Twitter twitter = new TwitterFactory().getInstance();
            do {
                followers = twitter.getFollowersList("srf", cursor);
                for (User follower : followers) {
                    // TODO: Collect top 10 followers here
                    System.out.println(follower.getName() + " has " + follower.getFollowersCount() + " follower(s)");
                }
            } while ((cursor = followers.getNextCursor()) != 0);
        } catch (TwitterException e) {
            LOG.error("Got TwitterException while fetching: {}", e.getErrorMessage());
        }


        return graph;
    }
}
