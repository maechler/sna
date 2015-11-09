package ch.fhnw.sna.examples.dbpedia;

import static ch.fhnw.sna.util.GenreDetectionUtil.isBlues;
import static ch.fhnw.sna.util.GenreDetectionUtil.isCountry;
import static ch.fhnw.sna.util.GenreDetectionUtil.isElectronic;
import static ch.fhnw.sna.util.GenreDetectionUtil.isFolk;
import static ch.fhnw.sna.util.GenreDetectionUtil.isHipHop;
import static ch.fhnw.sna.util.GenreDetectionUtil.isJazz;
import static ch.fhnw.sna.util.GenreDetectionUtil.isLatin;
import static ch.fhnw.sna.util.GenreDetectionUtil.isPop;
import static ch.fhnw.sna.util.GenreDetectionUtil.isRock;
import static ch.fhnw.sna.util.GenreDetectionUtil.isSoul;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Iterator;
import java.util.Set;

import org.apache.jena.datatypes.xsd.XSDDatatype;
import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.datatypes.xsd.impl.XSDDateType;
import org.apache.jena.datatypes.xsd.impl.XSDYearType;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import ch.fhnw.sna.examples.dbpedia.model.MusicArtist;
import ch.fhnw.sna.examples.dbpedia.model.MusicArtistGraph;

/**
 * Fetches the Music Artist Association Network from dbpedia
 * 
 */
public class MusicArtistFetcher {

	private static final String DBPEDIA_SPARQL_ENDPOINT = "http://dbpedia.org/sparql";
	private static final DateTimeFormatter ACTIVE_YEAR_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	private static final Logger LOG = LoggerFactory.getLogger(MusicArtistFetcher.class);

	public MusicArtistGraph fetch() {
		MusicArtistGraph graph = new MusicArtistGraph();
		LOG.info("Start fetching Music Artist Network");
		fetchAssociations(graph);
		LOG.info("Fiinished fetching Music Artist Network");
		LOG.info("Start fetching node attributs");
		enrichNodeInformation(graph);
		LOG.info("Finished fetching node attributes");
		return graph;
	}

	private void fetchAssociations(MusicArtistGraph graph) {
		final int LIMIT = Integer.MAX_VALUE; // Means no limit
		boolean hasMoreResults = true;
		int currentOffset = 0;
		int fetchedTotal = 0;
		while (hasMoreResults && fetchedTotal < LIMIT) {
			String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
					+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
					+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> \n" +

			"SELECT ?sourcename ?targetname ?sourceuri ?targeturi \n" + "WHERE { "
					+ "?sourceuri a <http://dbpedia.org/ontology/MusicalArtist>."
					+ "?targeturi a dbpedia-owl:MusicalArtist ."
					+ "?sourceuri dbpedia-owl:associatedMusicalArtist	?targeturi."
					+ "?sourceuri rdfs:label ?sourcename ." + "?targeturi rdfs:label ?targetname ."
					+ "FILTER langMatches( lang(?sourcename), \"en\" ) . \n"
					+ "FILTER langMatches( lang(?targetname), \"en\" )" + "} LIMIT 1000 OFFSET " + currentOffset;

			LOG.debug("Querying: {}", queryString);

			Query query = QueryFactory.create(queryString);
			int resultCounter = 0;
			try (QueryExecution qexec = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT, query)) {
				ResultSet results = qexec.execSelect();

				while (results.hasNext()) {
					++resultCounter;
					QuerySolution sol = results.next();
					String fromUri = sol.getResource("sourceuri").getURI();
					String toUri = sol.getResource("targeturi").getURI();
					String from = sol.getLiteral("sourcename").getLexicalForm();
					String to = sol.getLiteral("targetname").getLexicalForm();
					graph.addArtistIfNotExists(fromUri, from);
					graph.addArtistIfNotExists(toUri, to);
					graph.addAssociation(fromUri, toUri);
				}
			}
			LOG.debug("Fetches {} new results.", resultCounter);
			fetchedTotal += resultCounter;
			currentOffset += 1000;
			if (resultCounter < 1000) {
				hasMoreResults = false;
			}
		}
	}

	private void enrichNodeInformation(MusicArtistGraph graph) {
		for (MusicArtist a : graph.getArtists()){
			enrichSingleArtist(a);
		}
	}

	private void enrichSingleArtist(MusicArtist artist) {
		LOG.info("Enrich artist {}", artist.getLabel());
		String queryString = buildActorQuery(artist);

		Query query = QueryFactory.create(queryString);
		Set<String> genres = Sets.newHashSet();
		Set<String> subjects = Sets.newHashSet();

		try (QueryExecution qexec = QueryExecutionFactory.sparqlService(DBPEDIA_SPARQL_ENDPOINT, query)) {

			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution sol = results.next();
				Iterator<String> it = sol.varNames();
				while (it.hasNext()) {
					String key = it.next();
					switch (key) {
					case "yearsActive":
						Literal activeYears = sol.getLiteral(key);
						extractActiveYears(artist, activeYears);
						break;
					case "birthdate":
						Literal birthdate = sol.getLiteral(key);
						extractBirthdate(artist, birthdate);
						break;

					case "genre":
						Resource genre = sol.getResource(key);
						genres.add(genre.getLocalName());
						break;

					case "subject":
						Resource subject = sol.getResource(key);
						subjects.add(subject.getLocalName());
						break;

					case "filmsubject":
						artist.setIsFilmActor(true);
						break;

					default:
						throw new IllegalStateException("Unknown key: " + key);
					}
				}
			}
		}

		extractArtistGenres(artist, genres);
		extractArtistSex(artist, subjects);
	}

	private void extractBirthdate(MusicArtist artist, Literal birthdate) {
		try {
		if (birthdate.getDatatype() instanceof XSDDateType) {
			XSDDateTime time = (XSDDateTime) birthdate.getValue();
			LocalDate birthDate = LocalDate.of(time.getYears(), time.getMonths(), time.getDays());
			artist.setBirthdate(birthDate);
		} else if (XSDDatatype.XSDgYear.equals(birthdate.getDatatype())) {
			int year = Integer.parseInt(birthdate.getValue().toString());
			artist.setBirthdate(LocalDate.of(year, 1, 1));
		
		} else if (birthdate.getDatatype() instanceof XSDYearType) {
			LocalDate birthDate = LocalDate.parse(birthdate.getLexicalForm(), ACTIVE_YEAR_FORMATTER);
			artist.setBirthdate(LocalDate.of(birthDate.getYear(), 1, 1));
		}
		else {
			LOG.error("Unknown birthdate type: " + birthdate.getDatatype());
		}
		} catch (DateTimeParseException ex) {
			LOG.warn("Could not extract time from: "+birthdate);
		}
	}

	private void extractActiveYears(MusicArtist artist, Literal years) {
		int year =-1;
			if (XSDDatatype.XSDgYear.equals(years.getDatatype())){
				year = Integer.parseInt(years.getValue().toString());
			} else {
				LOG.warn("Could not extract time datatype: "+years);
			}
		if (year > -1){
			artist.setActiveYears(LocalDate.now().getYear() - year);
		}
	}

	private void extractArtistGenres(MusicArtist artist, Set<String> genres) {
		// Main music genres from:
		// https://en.wikipedia.org/wiki/List_of_popular_music_genres

		for (String genre : genres) {
			String lowerCase = genre.toLowerCase();
			if (isBlues(lowerCase)) {
				artist.addGenre("Blues");
			} else if (isCountry(lowerCase)) {
				artist.addGenre("Country");
			} else if (isElectronic(lowerCase)) {
				artist.addGenre("Electronic");
			} else if (isFolk(lowerCase)) {
				artist.addGenre("Folk");
			} else if (isHipHop(lowerCase)) {
				artist.addGenre("Hip Hop");
			} else if (isJazz(lowerCase)) {
				artist.addGenre("Jazz");
			} else if (isLatin(lowerCase)) {
				artist.addGenre("Latin");
			} else if (isPop(lowerCase)) {
				artist.addGenre("Pop");
			} else if (isRock(lowerCase)) {
				artist.addGenre("Rock");
			} else if (isSoul(lowerCase)) {
				artist.addGenre("Soul");
			} else {
				LOG.debug("Could not find matching genre for: " + genre);
			}
		}

	}

	private void extractArtistSex(MusicArtist artist, Set<String> subjects) {
		int maleCounter = 0;
		int femaleCounter = 0;
		for (String subject : subjects) {
			if (subject.contains("male")) {
				++maleCounter;
			}
			if (subject.contains("female")) {
				++femaleCounter;
			}
		}
		if (femaleCounter > 0) {
			artist.setSex("female");
		} else if (maleCounter > 0) {
			artist.setSex("male");
		} else {
			LOG.debug("No sex found for: " + artist);
		}
	}

	private String buildActorQuery(MusicArtist artist) {
		String artistUri = artist.getUri();
		String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> \n"
				+ "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> \n"
				+ "PREFIX dbpedia-owl: <http://dbpedia.org/ontology/> \n" + "SELECT * \n" + "WHERE { " + "{<"
				+ artistUri + "> dbpedia-owl:activeYearsStartYear ?yearsActive} \n UNION \n" + "{<" + artistUri
				+ "> dbpedia-owl:birthDate ?birthdate} \n UNION \n" + "{<" + artistUri
				+ "> dbpedia-owl:genre ?genre} \n UNION \n" + "{<" + artistUri
				+ "> <http://purl.org/dc/terms/subject> ?subject} \n UNION \n" + "{<" + artistUri
				+ "> a ?filmsubject . \n"
				+ "?filmsubject rdfs:subClassOf <http://dbpedia.org/class/yago/Actor109765278>" + "}}";

		LOG.debug("Querying: {}", queryString);
		return queryString;
	}
}
