package ch.fhnw.sna.twitter;

import java.io.IOException;
import java.util.ArrayList;

import ch.fhnw.sna.twitter.database.DatabaseAccess;
import ch.fhnw.sna.twitter.model.NewsportalGraph;
import ch.fhnw.sna.twitter.model.TwitterUser;
import ch.fhnw.sna.twitter.utility.IO;
import com.almworks.sqlite4java.SQLiteException;
import twitter4j.TwitterException;

public class NewsportalFetcher_Main {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(NewsportalFetcher_Main.class);

    public static void main(String[] args) throws TwitterException, InterruptedException, IOException, SQLiteException {
        if (args.length == 0) {
            LOG.error("Must provide an argument: fetch, export");

            return;
        }

        switch (args[0]) {
            case "fetch":
                ArrayList<String> newsportals = new ArrayList<String>() {};
                newsportals.add("tagesanzeiger");
                newsportals.add("watson_news");
                newsportals.add("Weltwoche");
                newsportals.add("blickamabend");
                newsportals.add("Blickch");
                newsportals.add("NZZ");
                newsportals.add("20min");

                if (args.length < 2) {
                    LOG.error("Must specify two arguments for command fetch: fetch ids, fetch newsportals, fetch newsportalFollowers, fetch humanFollowers");
                } else {
                    switch (args[1]) {
                        case "ids":
                            fetchIds(args, newsportals);
                            break;
                        case "newsportals":
                            fetchNewsportals(args, newsportals);
                            break;
                        case "newsportalFollowers":
                            fetchNewsportalFollowers(args, newsportals);
                            break;
                        case "humanFollowers":
                            fetchHumanFollowers(args);
                            break;
                        default:
                            LOG.error("Invalid command: fetch " + args[1]);
                    }
                }
                break;
            case "export":
                export(args);
                break;
            default:
                LOG.error("Invalid command provided: " + args[0]);
        }
    }

    private static void fetchIds(String[] args, ArrayList<String> newsportals) throws SQLiteException, IOException, TwitterException, InterruptedException {
        LOG.info("Method started: fetch ids");

        DatabaseAccess db = new DatabaseAccess();
        NewsportalFetcher newsportalFetcher = new NewsportalFetcher(db);

        newsportalFetcher.fetchIds(newsportals);
        newsportalFetcher.pickRandomIDs(newsportals);

        db.close();

        LOG.info("Method finished: fetch ids");
    }

    private static void fetchHumanFollowers(String[] args) throws SQLiteException, IOException, TwitterException, InterruptedException {
        LOG.info("Method started: fetch humanFollowers");

        DatabaseAccess db = new DatabaseAccess();
        NewsportalFetcher newsportalFetcher = new NewsportalFetcher(db);

        newsportalFetcher.fetchHumanFollowers();
        db.close();

        LOG.info("Method finished: fetch humanFollowers");
    }

    private static void fetchNewsportalFollowers(String[] args, ArrayList<String> newsportals) throws SQLiteException, IOException, TwitterException, InterruptedException {
        LOG.info("Method started: fetch newsportalFollowers");

        DatabaseAccess db = new DatabaseAccess();
        NewsportalFetcher newsportalFetcher = new NewsportalFetcher(db);

        newsportalFetcher.fetchHumans(newsportals);
        db.close();

        LOG.info("Method finished: fetch newsportalFollowers");
    }

    private static void fetchNewsportals(String[] args, ArrayList<String> newsportals) throws SQLiteException, IOException, TwitterException {
        LOG.info("Method started: fetch newsportals");

        DatabaseAccess db = new DatabaseAccess();
        NewsportalFetcher newsportalFetcher = new NewsportalFetcher(db);

        newsportalFetcher.fetchNewsportals(newsportals);
        db.close();

        LOG.info("Method finished: fetch newsportals");
    }

    private static void export(String[] args) throws SQLiteException, IOException {
        LOG.info("Method started: export");
        String filename = "Newsportals.gexf";
        DatabaseAccess db = new DatabaseAccess();

        if (args.length < 2) {
            LOG.info("No filename specified, using default " + filename);
        } else {
            filename = args[1];
        }

        new NewsportalGephiExport(filename).export(NewsportalGraph.createFromDB(db));
        db.close();

        LOG.info("Method finished: export");
    }
}
