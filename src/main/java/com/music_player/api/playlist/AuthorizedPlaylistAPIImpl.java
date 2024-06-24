//$Id$
package com.music_player.api.playlist;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONObject;

import com.music_player.api.common.DuplicateException;
import com.music_player.api.playlist.util.PlaylistUtil;
import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;
import com.music_player.api.songqueue.util.SongQueueUtil;

public class AuthorizedPlaylistAPIImpl implements PlaylistAPI{
	
	private PlaylistAPI playlistAPI = new PlaylistAPIImpl();

	@Override
	public boolean createPlayList(int userId, String playListName) throws Exception {
		// validate userID
		// check if user has a playlist with the same name
		if(PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playListName)) {
			throw new DuplicateException("Playlist with name " + playListName + " exists");
		}
		return playlistAPI.createPlayList(userId, playListName);
	}

	@Override
	public boolean addSongToPlayList(int userId, int playlistId, int songId) throws Exception {
		// validate userId
		// check if user has this playlist
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		} else if(!PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playlistId)) {
			throw new NullPointerException("Playlist not found");
		} else if (PlaylistUtil.getInstance().checkIfSongInPlaylist(songId, playlistId)) {
			throw new DuplicateException("Song already present in playlist");
		}
		return playlistAPI.addSongToPlayList(userId, playlistId, songId);
	}

	@Override
	public boolean addCurrentPlayingSongToPlayList(int userId, int playlistId) throws DuplicateException, Exception {
		Song currentPlayingSong = SongQueueUtil.getInstance().getCurrentPlayingSong(userId);
		if(currentPlayingSong == null) {
			throw new NullPointerException("No Song playing right now");
		} else if(!PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playlistId)) {
			throw new NullPointerException("Playlist not found");
		} else if (PlaylistUtil.getInstance().checkIfSongInPlaylist(currentPlayingSong.getSongId(), playlistId)) {
			throw new DuplicateException("Song already present in playlist");
		}
		return playlistAPI.addCurrentPlayingSongToPlayList(userId, playlistId);
	}

	@Override
	public boolean removeSongFromPlaylist(int userId, int songId, int playlistId) throws DuplicateException, Exception {
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		} else if(!PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playlistId)) {
			throw new NullPointerException("Playlist not found");
		} else if (!PlaylistUtil.getInstance().checkIfSongInPlaylist(songId, playlistId)) {
			throw new NullPointerException("Song not present in playlist");
		}
		return playlistAPI.removeSongFromPlaylist(userId, songId, playlistId);
	}

	@Override
	public List<Song> getPlaylistSongs(int userId, int playlistId) throws Exception {
		if(!PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playlistId)) {
			throw new NullPointerException("Playlist not found");
		}
		return playlistAPI.getPlaylistSongs(userId, playlistId);
	}

	@Override
	public boolean renamePlaylist(int userId, int playlistId, String newPlayListName) throws Exception {
		// validate user
		if(!PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playlistId)) {
			throw new NullPointerException("Playlist not found");
		} else if(PlaylistUtil.getInstance().checkIfPlaylistExists(userId, newPlayListName)) {
			throw new DuplicateException("Playlist with name " + newPlayListName + " exists");
		}
		return playlistAPI.renamePlaylist(userId, playlistId, newPlayListName);
	}

	@Override
	public boolean deletePlaylist(int userId, int playlistId) throws Exception {
		if(!PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playlistId)) {
			throw new NullPointerException("Playlist not found");
		}
		return playlistAPI.deletePlaylist(userId, playlistId);
	}

	@Override
	public Song playPlaylist(int userId, int playListId) throws Exception {
		if(!PlaylistUtil.getInstance().checkIfPlaylistExists(userId, playListId)) {
			throw new NullPointerException("Playlist not found");
		} else if(PlaylistUtil.getInstance().getPlaylistSongs(playListId).isEmpty()) {
			return null;
		}
		return playlistAPI.playPlaylist(userId, playListId);
	}
	
	@Override
	public JSONObject getPlaylistListing(int userId) throws SQLException {
		// validate user
		return playlistAPI.getPlaylistListing(userId);
	}
}
