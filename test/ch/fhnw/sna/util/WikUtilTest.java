package ch.fhnw.sna.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class WikUtilTest {

	@Test
	public void testHumanCategories() {
		String[] expectedHumanCategories = new String[] { "1858 births",
				"1918 deaths", "1858 births", "1917 deaths", "Living people",
				"Dead people" };
		// Check without category prefix
		for (String category : expectedHumanCategories) {
			assertTrue("Failed while checking: " + category,
					WikiUtil.isHumanCategory("Category:" + category));
		}

	}

	@Test
	public void testNonHumanCategories() {
		String[] nonHumanCategories = new String[] { "Sociology",
				"Social sciences", "Sociological terminology",
				"Members of the Oberlin Group", "Wesleyan University",
				"Liberal arts colleges in Connecticut", "New England",
				"Small College" };

		for (String category : nonHumanCategories) {
			assertFalse(WikiUtil.isHumanCategory(category));
		}
	}

}
