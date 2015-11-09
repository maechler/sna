package ch.fhnw.sna.util;

public final class WikiUtil {

	private WikiUtil(){}
	
	public static boolean isHumanCategory(String categoryName){
		return categoryName.toLowerCase().matches(
				"category:\\d+\\sbirths|category:\\d+\\sdeaths|category:living people|category:dead people");
	}
}
