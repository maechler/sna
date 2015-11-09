package ch.fhnw.sna.twitter;

import ch.fhnw.sna.twitter.model.NewsportalGraph;

public class NewsportalFetcher_Main {
    public static void main(String[] args) {
        String FILE = "Newsportals.gexf";

        NewsportalGraph graph = new NewsportalFetcher().fetch();
        new NewsportalGephiExport(FILE).export(graph);
    }
}
