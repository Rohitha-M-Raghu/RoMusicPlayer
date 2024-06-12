//$Id$
package com.music_player.api.playlist;

import java.sql.SQLException;
import java.util.List;

import com.music_player.api.common.DuplicateException;
import com.music_player.api.song.util.Song;

public interface PlaylistAPI {

	boolean createPlayList(int userId, String playListName) throws SQLException, DuplicateException, Exception;

	boolean addSongToPlayList(int userId, int playlistId, int songId) throws SQLException, Exception;

	boolean addCurrentPlayingSongToPlayList(int userId, int playlistId) throws SQLException, DuplicateException, Exception;

	boolean removeSongFromPlaylist(int userId, int songId, int playlistId) throws SQLException, DuplicateException, Exception;

	List<Song> getPlaylistSongs(int userId, int playlistId) throws SQLException, Exception;

	boolean renamePlaylist(int userId, int playlistId, String newPlayListName) throws SQLException, Exception;

	boolean deletePlaylist(int userId, int playlistId) throws SQLException, Exception;

	Song playPlaylist(int userId, int playListId) throws Exception;

}
