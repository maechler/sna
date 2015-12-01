package ch.fhnw.sna.twitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        boolean analyseAllOverlaps = true;
        boolean analyseSpecificOverlaps = false;
        
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
        
        if(analyseAllOverlaps) {
            
            int[] list = new AnalyseFollowerOverlaps().analyseUserOverlapsAllNewsportals(newsportals);
            int i = 0;
            for(int occurs : list)
            {
                System.out.println("Followers with connection to "+(i++)+" of "+newsportals.size()+" newsportals: "+occurs);
            }
        }
        
        if(analyseSpecificOverlaps)
        {
            Map<String, List<String>> blubb = new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("tagesanzeiger","watson_news");
            System.out.println(blubb);
        }
    }
}
