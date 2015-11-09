package ch.fhnw.sna.twitter.model;

import twitter4j.User;

import java.util.List;

public class TwitterUser {
    protected List<TwitterUser> followings;
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

    public List<TwitterUser> getFollowings() {
        return followings;
    }

    public void setFollowings(List<TwitterUser> followings) {
        this.followings = followings;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }
}
