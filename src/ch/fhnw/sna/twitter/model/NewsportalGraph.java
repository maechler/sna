package ch.fhnw.sna.twitter.model;

import ch.fhnw.sna.twitter.database.DatabaseAccess;
import com.almworks.sqlite4java.SQLiteException;
import javafx.util.Pair;

import java.text.ParseException;
import java.util.*;

public class NewsportalGraph {
    private Map<Long, TwitterUser> newsportals = new HashMap<>();
    private Map<Long, TwitterUser> humans = new HashMap<>();
    private Map<Long, Set<Long>> associations = new HashMap<>();
    private static boolean onlyNewsportalFollowings = true;

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
        List<Pair<Long,Long>> edges = db.findAllEdges();
        List<Long> newsportalIds = new ArrayList<>();

        for (TwitterUser user: users) {
            graph.addNode(user);

            if (user.getType().equals(TwitterUser.TYPE_NEWSPORTAL)) {
                newsportalIds.add(user.getId());
            }
        }

        for (Pair<Long,Long> pair : edges) {
            if (onlyNewsportalFollowings) {
                if (newsportalIds.contains(pair.getValue())) {
                    graph.addEdge(pair.getKey(), pair.getValue());
                }
            } else {
                graph.addEdge(pair.getKey(), pair.getValue());
            }
        }

        return graph;
    }
}
