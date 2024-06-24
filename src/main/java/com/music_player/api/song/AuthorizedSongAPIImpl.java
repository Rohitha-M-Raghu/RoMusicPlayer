//$Id$
package com.music_player.api.song;

import java.sql.SQLException;
import java.util.List;

import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;

public class AuthorizedSongAPIImpl implements SongAPI{
	
	private SongAPI songAPI = new SongAPIImpl();
	
	@Override
	public Song getSongDetails(int songId, boolean isIncludeSongUrl) throws Exception {
		// check if user exists - decrpt the token from cookie
		// check if songId exists
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		}
		
		return songAPI.getSongDetails(songId, isIncludeSongUrl);
	}
	
	@Override
	public Song getCurrentPlatingSong(int userId) throws SQLException {
		// validate userId
		return songAPI.getCurrentPlatingSong(userId);
	}
	
	@Override
	public List<Song> getSongs(boolean isIncludeSongUrl) throws SQLException {
		return songAPI.getSongs(isIncludeSongUrl);

	}
	
	@Override
	public String getSongLyrics(int songId) throws Exception {
		// check if user exists
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		}
		return songAPI.getSongLyrics(songId);
	}

	@Override
	public boolean playSong(int userId, int songId) throws Exception {
		// check if user exists
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		}
		return songAPI.playSong(userId, songId);
	}

	@Override
	public boolean addSongToQueue(int userId, int songId) throws Exception {
		// check if user exists
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		}
		return songAPI.addSongToQueue(userId, songId);
	}

	@Override
	public boolean playSongNext(int userId, int songId) throws Exception {
		// check if user exists
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		}
		return songAPI.playSongNext(userId, songId);
	}

	@Override
	public boolean removeSongFromQueue(int userId, int songId, double order) throws Exception {
		// check if user exists
		if(order <= 0) {
			throw new IllegalArgumentException("Invalid order");
		}
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		} else if(!SongUtil.getInstance().isSongInQueue(userId, songId, order)) {
			throw new NullPointerException("Song not found in queue at order " + order);
		}
		return songAPI.removeSongFromQueue(userId, songId, order);
	}
	
	@Override
	public Song playAllSongs(int userId) throws SQLException {
		// validate user
		return songAPI.playAllSongs(userId);
	}
}
