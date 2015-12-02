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
        boolean analyseAllOverlaps = false;
        boolean analyseSpecificOverlaps = false;
        boolean analyseSpecificOverlaps5percent = false;
        
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
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("tagesanzeiger","watson_news");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("tagesanzeiger","Weltwoche");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("tagesanzeiger","blickamabend");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("tagesanzeiger","Blickch");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("tagesanzeiger","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("tagesanzeiger","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("watson_news","Weltwoche");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("watson_news","blickamabend");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("watson_news","Blickch");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("watson_news","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("watson_news","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("Weltwoche","blickamabend");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("Weltwoche","Blickch");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("Weltwoche","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("Weltwoche","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("blickamabend","Blickch");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("blickamabend","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("blickamabend","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("Blickch","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("Blickch","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals("NZZ","20min");
        }
        
        if(analyseSpecificOverlaps5percent)
        {
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","watson_news");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","Weltwoche");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","blickamabend");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","Blickch");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("tagesanzeiger","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","Weltwoche");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","blickamabend");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","Blickch");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("watson_news","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Weltwoche","blickamabend");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Weltwoche","Blickch");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Weltwoche","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Weltwoche","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("blickamabend","Blickch");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("blickamabend","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("blickamabend","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Blickch","NZZ");
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("Blickch","20min");
            
            new AnalyseFollowerOverlaps().analyseUserOverlapsSpecificNewsportals5percent("NZZ","20min");
        }
    }
}
