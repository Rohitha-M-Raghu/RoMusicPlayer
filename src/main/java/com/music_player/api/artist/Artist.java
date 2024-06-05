//$Id$
package com.music_player.api.artist;

public class Artist {
	private int artistId;
	private String artistName;
	private String description;
	private String country;
	private String genre;
	
	public int getArtistId() {
		return artistId;
	}

	public String getArtistName() {
		return artistName;
	}

	public String getDescription() {
		return description;
	}

	public String getCountry() {
		return country;
	}

	public String getGenre() {
		return genre;
	}

	private Artist(Builder builder) {
		this.artistId = builder.artistId;
		this.artistName = builder.artistName;
		this.description = builder.description;
		this.country = builder.country;
		this.genre = builder.genre;
	}
	
	public static class Builder {
		private int artistId;
		private String artistName;
		private String description;
		private String country;
		private String genre;
		
		public Builder(int artistId, String artistName) {
			this.artistId = artistId;
			this.artistName = artistName;
		}
		
		public Builder description(String description) {
			this.description = description;
			return this;
		}
		
		public Builder country(String country) {
			this.country = country;
			return this;
		}
		
		public Builder genre(String genre) {
			this.genre = genre;
			return this;
		}
		
		public Artist build() {
			return new Artist(this);
		}
	}
}
