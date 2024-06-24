//$Id$
package com.music_player.api.song;

import java.sql.SQLException;
import java.util.List;

import com.music_player.api.song.util.Song;

public interface SongAPI {

	Song getSongDetails(int songId, boolean isIncludeSongUrl) throws Exception;

	List<Song> getSongs(boolean isIncludeSongUrl) throws SQLException;

	boolean playSong(int userId, int songId) throws Exception;

	boolean addSongToQueue(int userId, int songId) throws Exception;

	boolean playSongNext(int userId, int songId) throws Exception;

	boolean removeSongFromQueue(int userId, int songId, double order) throws SQLException, Exception;

	String getSongLyrics(int songId) throws Exception;

	Song getCurrentPlatingSong(int userId) throws SQLException;

	Song playAllSongs(int userId) throws SQLException;

}
