//$Id$
package com.music_player.api.userpreference;

import java.sql.SQLException;

import org.apache.tomcat.websocket.AuthenticationException;

import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.userpreference.util.UserSettings;

public interface UserPreferenceAPI {

	void likeCurrentPlayingSong();

	void likeASong(int userId, int songId) throws Exception;

	void likeAPlayList(int userId, int playlistId);

	void changeSettings(int userId, SettingsMode setting, boolean newSettings) throws AuthenticationException, Exception;

	void displayFrequentlyPlayedSongs();

	UserSettings getSettings(int userId) throws SQLException;

	void displayFrequentlyPlayedGenre();

	void changeUserTheme();

	void displayMusicRecommendation();

	void unlikeASong(int userId, int songId) throws Exception;

	void unlikePlaylist(int userId, int playlistId);
	
	public void getLikedSongs(int userId);

	void getLikedPlayLists(int userId);

}
