//$Id$
package com.music_player.api.song.util;

import com.music_player.api.artist.Artist;

public class Song {
	private int songId;
	private String songTitle;
	private String duration;
	private String genre;
	private Artist artist;
    
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
    }
    
	public static class Builder {
		private int songId;
		private String songTitle;
		private String duration;
		private String genre;
		private Artist artist;
	    
	    public Builder(int songId, String songTitle) {
	    	this.songId = songId;
	    	this.songTitle = songTitle;
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
	    
	    public Song build() {
	    	return new Song(this);
	    }
	}
}
