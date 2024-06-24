//$Id$
package com.music_player.api.userpreference;

import java.sql.SQLException;
import java.util.List;

import org.apache.tomcat.websocket.AuthenticationException;

import com.music_player.api.song.util.Song;
import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.userpreference.util.UserSettings;

public interface UserPreferenceAPI {

	boolean likeCurrentPlayingSong(int userId) throws Exception;

	boolean likeASong(int userId, int songId) throws Exception;

	boolean likeAPlayList(int userId, int playlistId) throws SQLException, Exception;

	void changeSettings(int userId, SettingsMode setting, boolean newSettings) throws AuthenticationException, Exception;

	List<Song> getFrequentlyPlayedSongs(int userId, boolean isCountNeeded) throws SQLException;

	UserSettings getSettings(int userId) throws SQLException;

	void displayFrequentlyPlayedGenre();

	void changeUserTheme();

	void displayMusicRecommendation();

	boolean unlikeASong(int userId, int songId) throws Exception;

	boolean unlikePlaylist(int userId, int playlistId) throws Exception;
	
	public List<Song> getLikedSongs(int userId) throws SQLException;

	void getLikedPlayLists(int userId);

	Song playlikedSongs(int userId) throws SQLException;

}
