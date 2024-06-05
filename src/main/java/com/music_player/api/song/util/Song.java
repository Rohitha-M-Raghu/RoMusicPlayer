//$Id$
package com.music_player.api.song.util;

import com.music_player.api.artist.Artist;

public class Song {
	private int songId;
	private String songTitle;
	private String duration;
	private String genre;
	private Artist artist;
    private byte[] mp3File;
    
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

	public byte[] getMp3File() {
		return mp3File;
	}

	private Song(Builder builder) {
    	this.songId = builder.songId;
    	this.songTitle = builder.songTitle;
    	this.duration = builder.duration;
    	this.genre = builder.genre;
    	this.artist = builder.artist;
    	this.mp3File = builder.mp3File;
    }
    
	public static class Builder {
		private int songId;
		private String songTitle;
		private String duration;
		private String genre;
		private Artist artist;
	    private byte[] mp3File;
	    
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
	    
	    public Builder mp3File(byte[] mp3File) {
	    	this.mp3File = mp3File;
	    	return this;
	    }
	    
	    public Song build() {
	    	return new Song(this);
	    }
	}
}
