//$Id$
package com.music_player.api.userpreference;

import java.sql.SQLException;

import org.apache.tomcat.websocket.AuthenticationException;

import com.music_player.api.userauthentication.util.UserUtil;
import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.userpreference.util.UserSettings;

public class AuthorizedUserPreferenceAPIImpl implements UserPreferenceAPI{

	@Override
	public void likeCurrentPlayingSong() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void likeASong(int userId, int songId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void likeAPlayList(int userId, int playlistId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void changeSettings(int userId, SettingsMode setting, boolean newSettings) throws Exception {
		// validate userID
		if(!UserUtil.getInstance().isUserExist(userId)) {
			throw new AuthenticationException("Invalid username or password");
		}
		new UserPreferenceAPIImpl().changeSettings(userId, setting, newSettings);
	}

	@Override
	public void displayFrequentlyPlayedSongs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserSettings getSettings(int userId) throws SQLException {
		return new UserPreferenceAPIImpl().getSettings(userId);
		
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
	public void unlikeASong(int userId, int songId) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unlikePlaylist(int userId, int playlistId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getLikedSongs(int userId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void getLikedPlayLists(int userId) {
		// TODO Auto-generated method stub
		
	}

}
