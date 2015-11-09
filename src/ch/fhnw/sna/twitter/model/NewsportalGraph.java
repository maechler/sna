package ch.fhnw.sna.twitter.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NewsportalGraph {
    List<NewsportalTwitterUser> newsportals = new ArrayList<>();
    List<HumanTwitterUser> humans = new ArrayList<>();

    public void addNewsportal(NewsportalTwitterUser newsportal) {
        newsportals.add(newsportal);
    }

    public void addHuman(HumanTwitterUser human) {
        humans.add(human);
    }
}
