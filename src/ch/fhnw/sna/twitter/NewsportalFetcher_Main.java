package ch.fhnw.sna.twitter;

import java.util.ArrayList;

import ch.fhnw.sna.twitter.model.NewsportalGraph;

public class NewsportalFetcher_Main {
    public static void main(String[] args) {
        String FILE = "Newsportals.gexf";

        ArrayList<String> newsportals = new ArrayList<String>();
        
        // Newsportale
        newsportals.add("NZZ");
        newsportals.add("tagi");
        newsportals.add("Blickch");
        newsportals.add("watson_news");
        newsportals.add("blickamabend");
        newsportals.add("20min");
        newsportals.add("Weltwoche");
        newsportals.add("SRF");
        
        NewsportalGraph graph = new NewsportalFetcher().fetch(newsportals);
        new NewsportalGephiExport(FILE).export(graph);
    }
}
