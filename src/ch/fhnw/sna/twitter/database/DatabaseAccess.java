package ch.fhnw.sna.twitter.database;

import ch.fhnw.sna.twitter.model.TwitterUser;
import ch.fhnw.sna.twitter.utility.IO;
import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import javafx.util.Pair;
import twitter4j.PagableResponseList;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DatabaseAccess {
    private final String databaseFilePath = "database/data_fhnw";
    private final String databaseSchemaPath = "database/schema.sql";
    private SQLiteConnection db;

    public DatabaseAccess() throws IOException, SQLiteException {
        db = new SQLiteConnection(new File(databaseFilePath));

        db.open(true);
        db.exec("pragma encoding=\"UTF-8\";");
        db.exec(IO.readFile(databaseSchemaPath, StandardCharsets.UTF_8));
    }

    public void close() {
        db.dispose();
    }

    public void saveEdge(long from, long to) throws SQLiteException {
        SQLiteStatement st = db.prepare("INSERT OR REPLACE INTO edges VALUES(?,?)");

        try {
            st.bind(1, from);
            st.bind(2, to);

            st.step();
        } finally {
            st.dispose();
        }
    }

    public void saveNode(TwitterUser user) throws SQLiteException {
        SQLiteStatement st = db.prepare("INSERT OR REPLACE INTO nodes VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)");

        try {
            st.bind(1, user.getId());
            st.bind(2, user.getScreenName());
            st.bind(3, user.getFollowersCount());
            st.bind(4, user.getFollowingsCount());
            st.bind(5, user.getTweetsCount());
            st.bind(6, user.getDescription());
            st.bind(7, user.getName());
            st.bind(8, user.getLocation());
            st.bind(9, user.getLang());
            st.bind(10, user.getType());
            st.bind(11, user.getLoadedFollowers() ? "1" : "0");
            st.bind(12, user.getCreatedAt().toString());
            st.bind(13, user.getTweetsFavorite());

            st.step();
        } finally {
            st.dispose();
        }
    }

    public TwitterUser findNodeByScreenName(String screenName) throws SQLiteException, ParseException {
        SQLiteStatement st = db.prepare("SELECT * FROM nodes WHERE screenName=?");
        TwitterUser node;

        try {
            st.bind(1, screenName);
            st.step();

            node = createNodeFromStatement(st);
        } finally {
            st.dispose();
        }

        return node;
    }

    public TwitterUser findNodeById(long id) throws SQLiteException, ParseException {
        SQLiteStatement st = db.prepare("SELECT * FROM nodes WHERE id=?");
        TwitterUser node;

        try {
            st.bind(1, id);
            st.step();

            node = createNodeFromStatement(st);
        } finally {
            st.dispose();
        }

        return node;
    }

    private TwitterUser createNodeFromStatement(SQLiteStatement st) throws SQLiteException, ParseException {
        TwitterUser node = new TwitterUser(st.columnString(9));
        String target = "Thu Sep 28 20:29:30 JST 2000";
        DateFormat df = new SimpleDateFormat("EEE MMM dd kk:mm:ss z yyyy", Locale.ENGLISH);
        Date result =  df.parse(target);

        node.setId(st.columnLong(0));
        node.setScreenName(st.columnString(1));
        node.setFollowersCount(st.columnInt(2));
        node.setFollowingsCount(st.columnInt(3));
        node.setTweetsCount(st.columnInt(4));
        node.setDescription(st.columnString(5));
        node.setName(st.columnString(6));
        node.setLocation(st.columnString(7));
        node.setLang(st.columnString(8));
        node.setLoadedFollowers(st.columnInt(10) == 1);
        node.setCreatedAt(df.parse(st.columnString(11)));
        node.setTweetsFavorite(st.columnInt(12));

        return node;
    }

    public int countEdgesFrom(long from) throws SQLiteException {
        SQLiteStatement st = db.prepare("SELECT COUNT(*) FROM edges WHERE `from`=?");
        int count = 0;

        try {
            st.bind(1, from);
            st.step();

            count = st.columnInt(0);
        } finally {
            st.dispose();
        }

        return count;
    }

    public List<Pair<Long,Long>> findAllEdges() throws SQLiteException {
        List<Pair<Long,Long>> allEdges = new ArrayList<>();
        SQLiteStatement st = db.prepare("SELECT * FROM edges");

        try {
            while (st.step()) {
                allEdges.add(new Pair<>(st.columnLong(0), st.columnLong(1)));
            }
        } finally {
            st.dispose();
        }

        return allEdges;
    }

    public List<TwitterUser> findAllNodes() throws SQLiteException, ParseException {
        List<TwitterUser> allNodes = new ArrayList<>();
        SQLiteStatement st = db.prepare("SELECT * FROM nodes");

        try {
            while (st.step()) {
                allNodes.add(createNodeFromStatement(st));
            }
        } finally {
            st.dispose();
        }

        return allNodes;
    }

    public List<Long> findAllNodeIds() throws SQLiteException {
        List<Long> allNodeIds = new ArrayList<>();
        SQLiteStatement st = db.prepare("SELECT id FROM nodes");

        try {
            while (st.step()) {
                allNodeIds.add(st.columnLong(0));
            }
        } finally {
            st.dispose();
        }

        return allNodeIds;
    }
}
