package ch.fhnw.sna.util;

/**
 * Simple approach to group genres
 */
public final class GenreDetectionUtil {

	public static boolean isRock(String genre) {
		return genre.contains("rock") || genre.contains("punk")
				|| genre.contains("metal") || genre.contains("new_wave")
				|| genre.contains("grindcore") || genre.contains("grunge");
	}

	public static boolean isPop(String genre) {
		return genre.contains("pop");
	}

	public static boolean isLatin(String genre) {
		return genre.contains("latin");
	}

	public static boolean isJazz(String genre) {
		return genre.contains("jazz") || genre.contains("bop")
				|| genre.contains("swing");
	}

	public static boolean isHipHop(String genre) {
		return genre.contains("hip_hop") || genre.contains("reggae")
				|| genre.contains("rap");
	}

	public static boolean isFolk(String genre) {
		return genre.contains("folk");
	}

	public static boolean isElectronic(String genre) {
		return genre.contains("electronic") || genre.contains("dance")
				|| genre.contains("disco") || genre.contains("house")
				|| genre.contains("ambient") || genre.contains("trip_hop")
				|| genre.contains("hardcore") || genre.contains("techno")
				|| genre.contains("minimal") || genre.contains("drum_and_bass")
				|| genre.contains("breakbeat");
	}

	public static boolean isCountry(String genre) {
		return genre.contains("country") || genre.contains("bluegras");
	}

	public static boolean isBlues(String genre) {
		return genre.contains("blues") || genre.contains("gospel");
	}

	public static boolean isSoul(String genre) {
		return genre.contains("soul") || genre.equals("b")
				|| genre.contains("funk");
	}
}
