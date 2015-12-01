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
    
    public int[] analyseUserOverlapsAllNewsportals(ArrayList<String> newsportals) throws IOException
    {
        
        int[] duplicates = new int[newsportals.size()+1];
        
        List<String> ids = new ArrayList<>();
        for (String newsportal : newsportals) {
            BufferedReader in = new BufferedReader(new FileReader("data/"+newsportal+".txt"));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                ids.add(line);
            }

            in.close();
            
        }
        
        for(String id : ids)
        {
            int occurrences = Collections.frequency(ids, id);
            duplicates[occurrences] = duplicates[occurrences]+1;
        }
        
        
        return duplicates;
    }
    
    public Map<String, List<String>> analyseUserOverlapsSpecificNewsportals(String first, String last) throws IOException
    {
        
        ArrayList<String> newsportals = new ArrayList<String>();
        newsportals.add(first);
        newsportals.add(last);
        
        Map<String, List<String>> duplicates = new HashMap<String, List<String>>();
        
        List<String> ids = new ArrayList<>();
        for (String newsportal : newsportals) {
            
            duplicates.put(newsportal, new ArrayList<>());

           // System.out.println(newsportal);
            BufferedReader in = new BufferedReader(new FileReader("data/"+newsportal+".txt"));
            for (String line = in.readLine(); line != null; line = in.readLine()) {
                ids.add(line);
                duplicates.get(newsportal).add(line);
                
            }
            in.close();
            
        }
        
        duplicates.put("commonids", new ArrayList<>());
        
        for(String id : ids)
        {
            int occurrences = Collections.frequency(ids, id);
            if(occurrences>1)
            {
                // Wenn die ID in beiden newsportalen vorkommen, die ID aus der Liste der beiden Listen löschen und stattdessen der gemeinsamen Liste hinzufügen
                for (String newsportal : newsportals) {
                    int deleteindex = duplicates.get(newsportal).indexOf(id);
                    if(deleteindex>=0)
                    {
                        duplicates.get(newsportal).remove(deleteindex);
                        System.out.println("Removed id "+id+" index on "+deleteindex+" from "+newsportal+" and added to commonids");
                    }
                }
                
                duplicates.get("commonids").add(id);
                
            }
        }
        
        
        // Graph
        
        
        return duplicates;
    }
}
