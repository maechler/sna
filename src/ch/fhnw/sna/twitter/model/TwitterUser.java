package ch.fhnw.sna.twitter.model;

import twitter4j.User;

import java.util.Date;

public class TwitterUser {
    protected int followersCount = 0;
    protected int followingsCount = 0;
    protected int tweetsCount = 0;
    protected int tweetsFavorite = 0;
    protected boolean loadedFollowers = false;
    protected String description;
    protected String screenName;
    protected String name;
    protected String location;
    protected String lang;
    protected Date createdAt;
    protected long id;
    protected final String type;
    public static final String TYPE_HUMAN = "Human";
    public static final String TYPE_NEWSPORTAL = "Newsportal";

    public TwitterUser(String type) {
        this.type = type;
    }

    public TwitterUser(User user, String type) {
        id = user.getId();
        followersCount = user.getFollowersCount();
        description = user.getDescription();
        screenName = user.getScreenName();
        name = user.getName();
        location = user.getLocation();
        lang = user.getLang();
        tweetsCount = user.getStatusesCount();
        followingsCount = user.getFriendsCount();
        tweetsFavorite = user.getFavouritesCount();
        createdAt = user.getCreatedAt();

        this.type = type;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingsCount() {
        return followingsCount;
    }

    public int getTweetsCount() {
        return tweetsCount;
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

    public String getType() {
        return type;
    }

    public boolean getLoadedFollowers() {
        return loadedFollowers;
    }

    public long getId() {
        return id;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public void setFollowingsCount(int followingsCount) {
        this.followingsCount = followingsCount;
    }

    public void setTweetsCount(int tweetsCount) {
        this.tweetsCount = tweetsCount;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public void setLoadedFollowers(boolean loadedFollowers) {
        this.loadedFollowers = loadedFollowers;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getTweetsFavorite() {
        return tweetsFavorite;
    }

    public void setTweetsFavorite(int tweetsFavorite) {
        this.tweetsFavorite = tweetsFavorite;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
