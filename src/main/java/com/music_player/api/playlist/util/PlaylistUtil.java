//$Id$
package com.music_player.api.playlist.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;
import com.music_player.api.songqueue.util.SongQueueUtil;
import com.music_player.db.DBConnector;

public class PlaylistUtil {
	private final Connection conn;
	private String query;
	private PreparedStatement pstmt;
	private ResultSet res;
	
	public static PlaylistUtil getInstance() {
		return PlaylistUtilInstance.INSTANCE;
	}
	
	private static class PlaylistUtilInstance {
		private static final PlaylistUtil INSTANCE = new PlaylistUtil();
	}
	
	private PlaylistUtil() {
		this.conn = DBConnector.getInstance().getConnection();
	}
	
	public boolean addNewPlayList(int userId, String playListName) throws SQLException {
		query = "INSERT INTO PlayList_Details (USER_ID, PLAYLIST_NAME, TYPE) VALUES (?, ?, ?)";
		
		try {
	        pstmt = conn.prepareStatement(query);

	        pstmt.setInt(1, userId);
	        pstmt.setString(2, playListName);
	        pstmt.setString(3, "CUSTOM");

	        return pstmt.executeUpdate() > 0;
	    } catch (SQLException e) {
	        if (e.getErrorCode() == 1062) { // MySQL error code for duplicate entry
	            System.out.println("Playlist already exists.");
	            return false; 
	        } else {
	            throw e; 
	        }
	    }
	}

	public boolean addSongToPlayList(int playlistId, int songId) throws SQLException {
	    double order = getLastOrderOfPlayListSongs(playlistId) + 1;
	    query = "INSERT INTO PlayListSongMapping (PLAYLISTID, SONGID, `ORDER`) VALUES (?, ?, ?)";
        pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, playlistId);
        pstmt.setInt(2, songId);
        pstmt.setDouble(3, order);
        return pstmt.executeUpdate() > 0;
	}
	
	public double getLastOrderOfPlayListSongs(int playListId) throws SQLException {
		query = "SELECT MAX(`ORDER`) AS highest_order FROM PlayListSongMapping WHERE PLAYLISTID = ?";
		pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, playListId);
        res = pstmt.executeQuery();
        if (res.next()) {
            return res.getDouble("highest_order");
        }
        return 0;
	}
	
	public boolean removeSongFromPlaylist(int playlistId, int songId) throws SQLException {
        query = "DELETE FROM PlayListSongMapping WHERE PLAYLISTID = ? AND SONGID = ?";
        pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, playlistId);
        pstmt.setInt(2, songId);
        return pstmt.executeUpdate() > 0;
	}
	
	public List<Integer> getPlaylistSongs(int playlistId) throws SQLException {
			query = "SELECT *"
					+ "FROM PlayListSongMapping "
					+ "WHERE PLAYLISTID = ? "
					+ "ORDER BY `ORDER`;";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, playlistId);
			res = pstmt.executeQuery();
			if(!res.isBeforeFirst()) {
				return new ArrayList<Integer>();
			}
			List<Integer> songList = new ArrayList<>();
			while(res.next()) {
				songList.add(res.getInt("SONGID"));
			}
			return songList;
	 	}
	
	public boolean renamePlaylist(int userId, int playlistId, String newPlaylistName) throws SQLException {
		query = "UPDATE PlayList_Details SET PLAYLIST_NAME = ? WHERE PLAYLIST_ID = ? AND USER_ID = ?";
		try {
			pstmt = conn.prepareStatement(query);
			pstmt.setString(1, newPlaylistName);
			pstmt.setInt(2, playlistId);
			pstmt.setInt(3, userId);
			return pstmt.executeUpdate() > 0;
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) {
	            System.out.println("PlaylistName already exists...");
	            // handle this
	        } else {
	        	throw e;
	        }
		}
		return false;
	}
	
	public boolean deletePlaylist(int userId, int playlistId) throws SQLException {
		query = "UPDATE PlayList_Details SET IS_PRESENCE = 0 WHERE PLAYLIST_ID = ? AND USER_ID = ? ";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, playlistId);
		pstmt.setInt(2, userId);
		return pstmt.executeUpdate() > 0;
	}
	
	public boolean checkIfPlaylistExists(int userId, String playlistName) throws Exception {
		query = "SELECT COUNT(*) AS ISPLAYLISTEXISTS FROM PlayList_Details WHERE USER_ID = ? AND PLAYLIST_NAME = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		pstmt.setString(2, playlistName);
		res = pstmt.executeQuery();
		if(res.next()) {
			return res.getBoolean("ISPLAYLISTEXISTS");
		} else {
			throw new Exception();
		}
	}
	
	public boolean checkIfPlaylistExists(int userId, int playlistId) throws Exception {
		query = "SELECT COUNT(*) AS ISPLAYLISTEXISTS FROM PlayList_Details WHERE USER_ID = ? AND PLAYLIST_ID = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		pstmt.setInt(2, playlistId);
		res = pstmt.executeQuery();
		if(res.next()) {
			return res.getBoolean("ISPLAYLISTEXISTS");
		} else {
			throw new Exception();
		}
	}
	
	public boolean checkIfSongInPlaylist(int songId, int playlistId) throws Exception {
		query = "SELECT COUNT(*) AS ISSONGPRESENT FROM PlayListSongMapping WHERE PLAYLISTID = ? AND SONGID = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, playlistId);
		pstmt.setInt(2, songId);
		res = pstmt.executeQuery();
		if(res.next()) {
			return res.getBoolean("ISSONGPRESENT");
		} else {
			throw new Exception();
		}
	}
	
	public JSONObject getPlaylistListing(int userId) throws SQLException {
		query = "SELECT PlayList_Details.*, COUNT(*) AS SONGCOUNT "
				+ "FROM PlayList_Details "
				+ "JOIN PlayListSongMapping ON PlayList_Details.PLAYLIST_ID = PlayListSongMapping.PLAYLISTID "
				+ "WHERE PlayList_Details.USER_ID = ? "
				+ "GROUP BY PlayList_Details.PLAYLIST_ID";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
		JSONArray playlistListing = new JSONArray();
		while(res.next()) {
			JSONObject playlistData = new JSONObject();
			playlistData.put("playlistId", res.getInt("PlayList_Details.PLAYLIST_ID"));
			playlistData.put("playlistName", res.getString("PlayList_Details.PLAYLIST_NAME"));
			playlistData.put("imgUrl", "images/playlist-folder-image.jpg");
			playlistData.put("songCount", res.getInt("SONGCOUNT"));
			playlistListing.put(playlistData);
		}
		
		JSONObject playlistListingData = new JSONObject();
		playlistListingData.put("data", playlistListing);
		return playlistListingData;
	}
	
	public Song addAllPlaylistSongsToQueue(int userId, boolean isQueueCleared, int playListId) throws SQLException, NumberFormatException, IOException {
		double order;
		if(isQueueCleared) {
			order = 1;
		} else {
			order = SongQueueUtil.getInstance().getHighestOrder(userId) + 1;
		}
		
		List<Integer> songList = getPlaylistSongs(playListId);
		for (Integer songId : songList) {
	        SongQueueUtil.getInstance().addSongToQueue(userId, songId, order, playListId);
	        order++; // Increment the order for the next song
	    }
		if(isQueueCleared) {
			return SongUtil.getInstance().getSongDetails(songList.get(0), true);
		} else {
			return null;
		}
	}
}
