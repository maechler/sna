package ch.fhnw.sna.twitter.model;

import twitter4j.User;

import java.util.List;

public class NewsportalTwitterUser extends TwitterUser {
    protected List<TwitterUser> followers;

    public NewsportalTwitterUser(User user) {
        super(user);
    }

    public List<TwitterUser> getFollowers() {
        return followers;
    }

    public void setFollowers(List<TwitterUser> followers) {
        this.followers = followers;
    }
}
