package ch.fhnw.sna.twitter.model;

import java.util.*;

public class NewsportalGraph {
    private Map<String, NewsportalTwitterUser> newsportals = new HashMap<>();
    private Map<String, HumanTwitterUser> humans = new HashMap<>();
    private Map<String, Set<String>> associations = new HashMap<>();

    public void addNewsportal(NewsportalTwitterUser newsportal) {
        newsportals.put(newsportal.getScreenName(), newsportal);
    }

    public void addHuman(String newsportalScreenName, HumanTwitterUser human) {
        String screenName = human.getScreenName();

        if (!humans.containsKey(screenName)) {
            humans.put(screenName, human);
        }

        if (!associations.containsKey(screenName)) {
            HashSet<String> newsportals = new HashSet<>();
            newsportals.add(newsportalScreenName);
            associations.put(screenName, newsportals);
        } else {
            Set<String> newsportals = associations.get(screenName);
            newsportals.add(newsportalScreenName);
        }
    }

    public Map<String, NewsportalTwitterUser> getNewsportals() {
        return newsportals;
    }

    public Map<String, HumanTwitterUser> getHumans() {
        return humans;
    }

    public Map<String, Set<String>> getAssociations() {
        return associations;
    }
}
