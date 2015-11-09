package ch.fhnw.sna.examples.dbpedia;

import ch.fhnw.sna.examples.dbpedia.model.MusicArtistGraph;

/**
 * Main class for Music artist fetcher
 *
 */
public class MusicArtistFetcher_Main {

	public static void main(String[] args) {
		String FILE = "MusicArtist-associations.gexf";
		
		MusicArtistGraph graph = new MusicArtistFetcher().fetch();
		new MusicArtistGephiExport(FILE).export(graph);
	}
}
