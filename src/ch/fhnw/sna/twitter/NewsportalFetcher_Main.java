package ch.fhnw.sna.twitter;

import java.io.IOException;
import java.util.ArrayList;

import ch.fhnw.sna.twitter.model.NewsportalGraph;
import twitter4j.TwitterException;

public class NewsportalFetcher_Main {
    public static void main(String[] args) throws TwitterException, InterruptedException, IOException {
        String FILE = "Newsportals.gexf";
        ArrayList<String> newsportals = new ArrayList<String>();
        NewsportalFetcher newsportalFetcher = new NewsportalFetcher();
        NewsportalGraph graph = null;
        
        boolean fetchIds = false;
        boolean fetchUserinfoGenerateGraph = false;
        boolean analyseOverlaps = true;
        
        // Newsportale
        newsportals.add("tagesanzeiger");
        newsportals.add("watson_news");
        newsportals.add("Weltwoche");
        newsportals.add("blickamabend");
        newsportals.add("Blickch");
        newsportals.add("NZZ");
        newsportals.add("20min");
        //newsportals.add("SRF");

        if (fetchIds) {
            newsportalFetcher.fetchNewsportalFollowerIDsToFile(newsportals);
            newsportalFetcher.writeRandomIDsFromNewsportalFollowerIDsToFile(newsportals);
        }

        if(fetchUserinfoGenerateGraph) {
            graph = newsportalFetcher.fetchUserInfosToGraph(newsportals);
            new NewsportalGephiExport(FILE).export(graph);
        }
        
        if(analyseOverlaps) {
            System.out.println(new AnalyseFollowerOverlaps().analyseUserFollowsAll(newsportals));
        }
    }
}
