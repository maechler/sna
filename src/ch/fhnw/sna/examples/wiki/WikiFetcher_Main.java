package ch.fhnw.sna.examples.wiki;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

public class WikiFetcher_Main {

	public static void main(String[] args) throws IOException {

		WikiExpertFetcher fetcher = new WikiExpertFetcher("Formula_One_people", 1);
		
		Map<String, Set<String>> peopleReferences = fetcher.fetch();
		
		new WikiGephiPeopleExport("Formula_One.gexf")
				.export(peopleReferences);
		new WikiGephiBipartiteExport("Formula_One_Bipartite.gexf")
				.export(peopleReferences);
	}

}
