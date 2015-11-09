package ch.fhnw.sna.examples.wiki;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.fhnw.sna.util.Wiki;
import ch.fhnw.sna.util.WikiUtil;

import com.google.common.collect.Sets;

/**
 * Fetches all Wikipedia articles located in the given category (and subcategories)
 * and extracts the people co-occurring network. (assuming they know each other
 * if several people are mentioned at one article page).
 * 
 *
 */
public class WikiExpertFetcher {

	public static final String DEFAULT_WIKI_DOMAIN = "en.wikipedia.org";
	public static final int DEFAULT_DEPTH = 0;

	private final Wiki wiki;

	private final String wikiDomain = DEFAULT_WIKI_DOMAIN;
	private final String baseCategory;
	private final int depth;

	private static final Logger LOG = LoggerFactory
			.getLogger(WikiExpertFetcher.class);

	public WikiExpertFetcher(String baseCategory, int depth) {
		this.baseCategory = baseCategory;
		this.depth = depth;
		wiki = new Wiki(wikiDomain);
	}

	public Map<String, Set<String>> fetch() throws IOException {
		LOG.info("Start looking for articles in category {}", baseCategory);
		String[] allArticleTitles = allArticleTitlesIn(baseCategory, depth)
				.toArray(new String[] {});
		LOG.info("Found {} articles total", allArticleTitles.length);
		LOG.info("Start fetching referenced Wikipages");
		Map<String, String[]> referencedWikiPages = referencedWikilinks(allArticleTitles);
		LOG.info("Fetched all referencing wikilinks for {} articles.",
				referencedWikiPages.size());
		LOG.info("Start extracting people");
		Map<String, Set<String>> peoplePerArticle = extractPeoplePerArticle(referencedWikiPages);
		return peoplePerArticle;
	}

	private Map<String, Set<String>> extractPeoplePerArticle(
			Map<String, String[]> referencedWikiPages) throws IOException {
		Map<String, Set<String>> peoplePerArticle = new HashMap<String, Set<String>>();
		// Cache
		Set<String> nonPeoplePages = new HashSet<String>();
		// Cache
		Set<String> allPeoplePages = new HashSet<String>();

		int counter = 0;
		for (Map.Entry<String, String[]> articleReferences : referencedWikiPages
				.entrySet()) {
			LOG.info("Extracting people, {}% done",
					(int)((double) counter++ * 100 / referencedWikiPages.size()));
			Set<String> referencedPeople = new HashSet<String>();
			for (String referencedArticle : articleReferences.getValue()) {
				if (nonPeoplePages.contains(referencedArticle)) {
					LOG.debug("{} already in cache (non-human)",
							referencedArticle);
					continue;
				}
				if (allPeoplePages.contains(referencedArticle)) {
					LOG.debug("{} already in cache (human)", referencedArticle);
					referencedPeople.add(referencedArticle);
					continue;
				}
				String[] categories = wiki.getCategories(referencedArticle);
				for (String category : categories) {
					if (WikiUtil.isHumanCategory(category)) {
						referencedPeople.add(referencedArticle);
						allPeoplePages.add(referencedArticle);
						break;
					} else {
						nonPeoplePages.add(referencedArticle);
					}
				}
			}
			if (!referencedPeople.isEmpty()) {
				peoplePerArticle.put(articleReferences.getKey(),
						referencedPeople);
				referencedPeople = new HashSet<String>();
			}
		}

		return peoplePerArticle;

	}

	/**
	 * Returns all referenced wikipedia articles for each given wikipedia
	 * article
	 */
	private Map<String, String[]> referencedWikilinks(String[] allArticleTitles)
			throws IOException {
		Map<String, String[]> references = new HashMap<String, String[]>(
				allArticleTitles.length * 2);
		for (String title : allArticleTitles) {
			if (references.containsKey(title)) {
				// Do not fetch already fetched articles
				continue;
			}
			String[] links = wiki.getLinksOnPage(title);
			references.put(title, links);
		}
		return references;
	}

	private Set<String> allArticleTitlesIn(String baseCategory, int depth)
			throws IOException {
		Set<String> allArticles = Sets.newHashSet();
		Queue<String> queue = new ArrayDeque<String>();
		queue.add(baseCategory);

		Set<String> nextRoundQueue = Sets.newHashSet();
		for (int i = 0; i < depth; i++) {
			while (!queue.isEmpty()) {
				String next = queue.remove();
				allArticles.addAll(Arrays.asList(wiki.getCategoryMembers(
						next, new int[] { Wiki.MAIN_NAMESPACE })));
				nextRoundQueue.addAll(Arrays.asList(wiki.getCategoryMembers(
						next, new int[] { Wiki.CATEGORY_NAMESPACE })));
			}
			nextRoundQueue = nextRoundQueue.stream()
					.map(u -> u.replaceFirst("Category:", ""))
					.collect(Collectors.toSet());
			queue.addAll(nextRoundQueue);
			nextRoundQueue.clear();
		}
		return allArticles;

	}
}
