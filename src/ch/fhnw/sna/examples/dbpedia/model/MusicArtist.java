package ch.fhnw.sna.examples.dbpedia.model;

import java.time.LocalDate;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class MusicArtist {

	private final String uri;
	private String label;
	private int activeYears = 0;
	private LocalDate birthdate = null;
	private boolean isFilmActor = false;
	private String sex = "unknown";
	private Set<String> genres = Sets.newHashSet();

	public MusicArtist(String uri) {
		this.uri = uri;
	}

	public MusicArtist(String uri, String label) {
		this(uri);
		this.label = label;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MusicArtist other = (MusicArtist) obj;
		if (uri == null) {
			if (other.uri != null)
				return false;
		} else if (!uri.equals(other.uri))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artist [uri=" + uri + ", label=" + label + "]";
	}

	public String getUri() {
		return uri;
	}

	public void setActiveYears(int activeYears) {
		this.activeYears = activeYears;
	}

	public void setBirthdate(LocalDate birthdate) {
		this.birthdate = birthdate;
	}

	public void setIsFilmActor(boolean isFilmActor) {
		this.isFilmActor = isFilmActor;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public void addGenre(String genre) {
		genres.add(genre);
	}

	public String getSex() {
		return sex;
	}

	public int getActiveYears() {
		return activeYears;
	}

	public LocalDate getBirthDate() {
		return birthdate;
	}

	public boolean isFilmActor() {
		return isFilmActor;
	}

	public String getGenres() {
		return Joiner.on(';').join(genres);
	}

	public String getLabel() {
		return label;
	}

}
