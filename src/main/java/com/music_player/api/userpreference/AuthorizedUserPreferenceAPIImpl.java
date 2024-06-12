//$Id$
package com.music_player.api.userpreference;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.websocket.AuthenticationException;

import com.music_player.api.playlist.util.PlaylistUtil;
import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;
import com.music_player.api.userauthentication.util.UserUtil;
import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.userpreference.util.UserPreferenceUtil;
import com.music_player.api.userpreference.util.UserSettings;
import com.music_player.support.Support;

public class AuthorizedUserPreferenceAPIImpl implements UserPreferenceAPI{
	
	private UserPreferenceAPI userPreferenceAPI = new UserPreferenceAPIImpl();

	@Override
	public boolean likeCurrentPlayingSong(int userId) throws Exception {
		// validate if user exists
		return userPreferenceAPI.likeCurrentPlayingSong(userId);
	}

	@Override
	public boolean likeASong(int userId, int songId) throws Exception {
		// validate if user exists
		// check if songId exists
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		}
		// check how to handle case in which song is already liked
		return userPreferenceAPI.likeASong(userId, songId);
	}
	
	@Override
	public boolean unlikeASong(int userId, int songId) throws Exception {
		// validate if user exists
		// check if songId exists
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		}
		
		 List<Integer> likedSongIds = UserPreferenceUtil.getInstance().getLikedSongs(userId);
		if (likedSongIds.isEmpty()) {
			return true;
		}
		if(!likedSongIds.contains(songId)) {
			return true;
		}
		// check how to handle case in which song is already liked
		return userPreferenceAPI.unlikeASong(userId, songId);
	}

	@Override
	public boolean likeAPlayList(int userId, int playlistId) throws Exception {
		if(!PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playlistId)) {
			throw new NullPointerException("Playlist Not Found...");
		}
		if(UserPreferenceUtil.getInstance().getLikedPlayLists(userId).contains(playlistId)) {
			return true;
		} 
		return userPreferenceAPI.likeAPlayList(userId, playlistId);
	}
	
	@Override
	public boolean unlikePlaylist(int userId, int playlistId) throws Exception {
		if(!PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playlistId)) {
			throw new NullPointerException("Playlist Not Found...");
		}
		if(!UserPreferenceUtil.getInstance().getLikedPlayLists(userId).contains(playlistId)) {
			return true;
		} 
		return userPreferenceAPI.unlikePlaylist(userId, playlistId);
	}

	@Override
	public void changeSettings(int userId, SettingsMode setting, boolean newSettings) throws Exception {
		// validate userID
		if(!UserUtil.getInstance().isUserExist(userId)) {
			throw new AuthenticationException("Invalid username or password");
		}
		userPreferenceAPI.changeSettings(userId, setting, newSettings);
	}

	@Override
	public List<Song> getFrequentlyPlayedSongs(int userId, boolean isCountNeeded) throws SQLException {
		// validate if user exists
		return userPreferenceAPI.getFrequentlyPlayedSongs(userId, isCountNeeded);
	}

	@Override
	public UserSettings getSettings(int userId) throws SQLException {
		return userPreferenceAPI.getSettings(userId);
		
	}

	@Override
	public void displayFrequentlyPlayedGenre() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeUserTheme() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void displayMusicRecommendation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Song> getLikedSongs(int userId) throws SQLException {
		// check if user exists
		return userPreferenceAPI.getLikedSongs(userId);
		
	}

	@Override
	public void getLikedPlayLists(int userId) {
		// TODO Auto-generated method stub
		
	}

}
