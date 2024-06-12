//$Id$
package com.music_player.api.playlist;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.music_player.api.playlist.util.PlaylistUtil;
import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;
import com.music_player.api.songqueue.util.SongQueueUtil;
import com.music_player.api.userpreference.UserPreferenceAPIImpl;
import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.db.DBConnector;

public class PlaylistAPIImpl implements PlaylistAPI{
	
	@Override
	public boolean createPlayList(int userId, String playListName) throws SQLException {
		return PlaylistUtil.getInstance().addNewPlayList(userId, playListName);
		// if false - duplicate
	}
	
	@Override
	public boolean addSongToPlayList(int userId, int playlistId, int songId) throws SQLException {
		// add song to playlist
		return PlaylistUtil.getInstance().addSongToPlayList(playlistId, songId);
	}
	
	@Override
	public boolean addCurrentPlayingSongToPlayList(int userId, int playlistId) throws SQLException {
		Song currentPlayingSong = SongQueueUtil.getInstance().getCurrentPlayingSong(userId); 
		return PlaylistUtil.getInstance().addSongToPlayList(playlistId, currentPlayingSong.getSongId());
	}
	
	@Override
	public boolean removeSongFromPlaylist(int userId, int songId, int playlistId) throws SQLException {
		// add song to playlist
		return PlaylistUtil.getInstance().removeSongFromPlaylist(playlistId, songId);
	}
	
	@Override
	public List<Song> getPlaylistSongs(int userId, int playlistId) throws SQLException {
		List<Integer> songList = PlaylistUtil.getInstance().getPlaylistSongs(playlistId);
		if(songList.isEmpty()) {
			return new ArrayList<>();
		}
		
		StringBuilder songs = new StringBuilder();
		for(int i = 0; i < songList.size(); ++i) {
			songs.append(songList.get(i));
			if(i < songList.size() - 1) {
				songs.append(",");
			}
		}
		return SongUtil.getInstance().getSongs(false, songs.toString());
	}
	
	@Override
	public boolean renamePlaylist(int userId, int playlistId, String newPlayListName) throws SQLException {
		return PlaylistUtil.getInstance().renamePlaylist(userId, playlistId, newPlayListName);	
	}
	
	@Override
	public boolean deletePlaylist(int userId, int playlistId) throws SQLException {
		return PlaylistUtil.getInstance().deletePlaylist(userId, playlistId);	
	}
	
	@Override
	public Song playPlaylist(int userId, int playListId) throws SQLException {
		Connection conn = DBConnector.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			SongQueueUtil.getInstance().clearSongQueue(userId);
			Song firstSong = PlaylistUtil.getInstance().addAllPlaylistSongsToQueue(userId, true, playListId);
			SongQueueUtil.getInstance().setCurrentPlayingSong(userId, firstSong.getSongId(), 1.0);
			conn.commit();
			
			// shuffle off if on
			if(UserPreferenceAPIImpl.getInstance().getSettings(userId).isShuffle()) {
				UserPreferenceAPIImpl.getInstance().changeSettings(userId, SettingsMode.SHUFFLE, false);
			}
			return firstSong;
		} catch (Exception e) {
			conn.rollback();
		} finally {
			conn.setAutoCommit(true);
		}
		return null;
	}
}
