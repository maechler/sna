package ch.fhnw.sna.twitter;

import java.io.IOException;
import java.util.ArrayList;

import ch.fhnw.sna.twitter.model.NewsportalGraph;

public class NewsportalFetcher_Main {
    public static void main(String[] args) throws InterruptedException, IOException {
        String FILE = "Newsportals.gexf";

        ArrayList<String> newsportals = new ArrayList<String>();
        
        // Newsportale
        newsportals.add("tagi");
        newsportals.add("watson_news");
        newsportals.add("Weltwoche");
        newsportals.add("blickamabend");
        newsportals.add("Blickch");
        newsportals.add("NZZ");
        newsportals.add("20min");
        //newsportals.add("SRF");
        
        NewsportalGraph graph = new NewsportalFetcher().fetch(newsportals);
        
        new NewsportalFetcher().pickRandomIDs(newsportals);
        
        //new NewsportalGephiExport(FILE).export(graph);
    }
}
