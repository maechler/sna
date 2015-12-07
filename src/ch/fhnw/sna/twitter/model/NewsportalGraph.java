package ch.fhnw.sna.twitter.model;

import ch.fhnw.sna.twitter.database.DatabaseAccess;
import com.almworks.sqlite4java.SQLiteException;

import java.text.ParseException;
import java.util.*;

public class NewsportalGraph {
    private Map<Long, TwitterUser> newsportals = new HashMap<>();
    private Map<Long, TwitterUser> humans = new HashMap<>();
    private Map<Long, Set<Long>> associations = new HashMap<>();

    public void addNode(TwitterUser node) {
        if (node.getType().equals(TwitterUser.TYPE_HUMAN)) {
            humans.put(node.getId(), node);
        } else {
            newsportals.put(node.getId(), node);
        }
    }

    public void addEdge(Long from, Long to) {
        if (!associations.containsKey(from)) {
            HashSet<Long> followings = new HashSet<>();
            followings.add(to);

            associations.put(from, followings);
        } else {
            Set<Long> followings = associations.get(from);
            followings.add(to);
        }
    }

    public Map<Long, TwitterUser> getNewsportals() {
        return newsportals;
    }

    public Map<Long, TwitterUser> getHumans() {
        return humans;
    }

    public Map<Long, Set<Long>> getAssociations() {
        return associations;
    }

    public static NewsportalGraph createFromDB(DatabaseAccess db) throws SQLiteException, ParseException {
        NewsportalGraph graph = new NewsportalGraph();
        List<TwitterUser> users = db.findAllNodes();
        Map<Long, Long> edges = db.findAllEdges();

        for (TwitterUser user: users) {
            graph.addNode(user);
        }

        for (Long key : edges.keySet()) {
            graph.addEdge(key, edges.get(key));
        }

        return graph;
    }
}
