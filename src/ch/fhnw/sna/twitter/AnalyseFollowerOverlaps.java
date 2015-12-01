package ch.fhnw.sna.twitter;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnalyseFollowerOverlaps {
    
    public int[] analyseUserFollowsAll(ArrayList<String> newsportals) throws IOException
    {
        
        //Map<String, List<String>> duplicates = new HashMap<String, List<String>>();
        
        int[] duplicates = new int[newsportals.size()+1];
        
        
        List<String> ids = new ArrayList<>();
        for (String newsportal : newsportals) {
            
            //duplicates.put(newsportal, new ArrayList<>());
            

            System.out.println(newsportal);
            BufferedReader in = new BufferedReader(new FileReader("data/"+newsportal+".txt"));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                ids.add(line);
            }

            in.close();
            
        }
        
        //int counter = 0;
        for(String id : ids)
        {
            int occurrences = Collections.frequency(ids, id);
            
            duplicates[occurrences] = duplicates[occurrences]+1;
            
            System.out.println("ID "+id+" is following "+occurrences+" newsportals");
            //if(occurrences==newsportals.size())
            //{
            //    ++counter;
            //}
        }
        //System.out.println(counter+" Users are following all the newsportals");
        
        
        return duplicates;
    }
}
