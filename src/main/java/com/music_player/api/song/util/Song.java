//$Id$
package com.music_player.api.song.util;

import com.music_player.api.artist.Artist;

public class Song {
	private int songId;
	private String songTitle;
	private String duration;
	private String genre;
	private Artist artist;
	private String songUrl;
	private String imageUrl;
	private double order;
	private int count;
    
	public String getImageUrl() {
		return imageUrl;
	}

	public int getCount() {
		return count;
	}

	public double getOrder() {
		return order;
	}

	public long getSongDurationInSecs() {
		if(duration == null) {
			return -1;
		}
		String[] time = duration.split(":");
		return ((3600L * Integer.parseInt(time[0])) + 60L * Integer.parseInt(time[1]) + Integer.parseInt(time[2]))*1000;
	}
	
    public String getSongUrl() {
		return songUrl;
	}

	public int getSongId() {
		return songId;
	}

	public String getSongTitle() {
		return songTitle;
	}

	public String getDuration() {
		return duration;
	}

	public String getGenre() {
		return genre;
	}

	public Artist getArtist() {
		return artist;
	}

	private Song(Builder builder) {
    	this.songId = builder.songId;
    	this.songTitle = builder.songTitle;
    	this.duration = builder.duration;
    	this.genre = builder.genre;
    	this.artist = builder.artist;
    	this.songUrl = builder.songUrl;
    	this.order = builder.order;
    	this.count = builder.count;
    	this.imageUrl = builder.imageUrl;
    }
    
	public static class Builder {
		private int songId;
		private String songTitle;
		private String duration;
		private String genre;
		private Artist artist;
		private String songUrl;
		private double order;
		private int count;
		private String imageUrl;
	    
		public Builder(Song song) {
			this.songId = song.songId;
			this.songTitle = song.songTitle;
			this.duration = song.duration;
			this.genre = song.genre;
			this.artist = song.artist;
			this.songUrl = song.songUrl;
			this.count = song.count;
			this.imageUrl = song.imageUrl;
		}
		
	    public Builder(int songId, String songTitle) {
	    	this.songId = songId;
	    	this.songTitle = songTitle;
	    }
	    
	    public Builder(int songId, double order) {
	    	this.songId = songId;
	    	this.order = order;
	    }
	    
	    public Builder duration(String duration) {
	    	this.duration = duration;
	    	return this;
	    }
	    
	    public Builder genre(String genre) {
	    	this.genre = genre;
	    	return this;
	    }
	    
	    public Builder artist(Artist artist) {
	    	this.artist = artist;
	    	return this;
	    }
	    
	    public Builder songUrl(String url) {
	    	this.songUrl = url;
	    	return this;
	    }
	    
	    public Builder imageUrl(String imageUrl) {
	    	this.imageUrl = imageUrl;
	    	return this;
	    }
	    
	    public Builder order(Double order) {
	    	this.order = order;
	    	return this;
	    }
	    
	    public Builder count(int count) {
	    	this.count = count;
	    	return this;
	    }
	    
	    public Song build() {
	    	return new Song(this);
	    }
	}
}
