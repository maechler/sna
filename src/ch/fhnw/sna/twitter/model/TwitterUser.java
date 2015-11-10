package ch.fhnw.sna.twitter.model;

import twitter4j.User;

import java.util.List;

public class TwitterUser {
    protected int followersCount;
    protected String description;
    protected String screenName;
    protected String name;
    protected String location;
    protected String lang;

    public TwitterUser(User user) {
        followersCount = user.getFollowersCount();
        description = user.getDescription();
        screenName = user.getScreenName();
        name = user.getName();
        location = user.getLocation();
        lang = user.getLang();
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public String getDescription() {
        return description;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getLang() {
        return lang;
    }
}
