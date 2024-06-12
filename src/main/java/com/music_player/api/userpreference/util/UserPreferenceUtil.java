//$Id$
package com.music_player.api.userpreference.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.music_player.db.DBConnector;

public class UserPreferenceUtil {
	
	private final Connection conn;
	private String query;
	private PreparedStatement pstmt;
	private ResultSet res;
	
	public static UserPreferenceUtil getInstance() {
		return UserPreferenceUtilInstance.INSTANCE;
	}
	
	private static class UserPreferenceUtilInstance {
		private static final UserPreferenceUtil INSTANCE = new UserPreferenceUtil();
	}
	
	private UserPreferenceUtil() {
		this.conn = DBConnector.getInstance().getConnection();
	}
	
	public int getCurrentUserSettings(int userId) throws SQLException {
		query = "SELECT * FROM User_Preference WHERE USER_ID = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
	    if (res.next()) {
	        return res.getInt("MODE");
	    }
	    return -1;
	}
	
	public boolean updateSettings(int userId, int valueToUpdate) throws SQLException {
		query = "UPDATE User_Preference SET MODE = ? WHERE USER_ID = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, valueToUpdate);
	    pstmt.setInt(2, userId);
	    return pstmt.executeUpdate() > 0;
	}
	
	public boolean likeASong(int userId, int songId) throws SQLException {
		if(songId != -1) {
			query = "INSERT INTO User_Liked_Songs (USER_ID, SONG_ID) VALUES (?, ?)";
	        pstmt = conn.prepareStatement(query);
	        pstmt.setInt(1, userId);
	        pstmt.setInt(2, songId);
	        pstmt.executeUpdate();
	        return true;
		}
		return false;
	}
	
	public List<Integer> getLikedSongs(int userId) throws NumberFormatException, SQLException {
		List<Integer> likedSongIds = new ArrayList<>();
		query = "SELECT * FROM User_Liked_Songs WHERE USER_ID = ?";
		pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, userId);
        res = pstmt.executeQuery();
        while(res.next()) {
        	likedSongIds.add(res.getInt("SONG_ID"));
        }
        return likedSongIds;
	}
	
	public boolean unlikeASong(int userId, int songId) throws SQLException {
		if(songId != -1) {
			query = "DELETE FROM User_Liked_Songs WHERE USER_ID = ? AND SONG_ID = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userId);
			pstmt.setInt(2, songId);
			return pstmt.executeUpdate() > 0;
		}
		return false;
	}
	
	public List<Integer> getLikedPlayLists(int userId) throws SQLException {
		List<Integer> likedPlaylists = new ArrayList<>();
		query = "SELECT * FROM PlayList_Details WHERE USER_ID = ? AND IS_LIKED = 1 AND IS_PRESENCE = 1";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
		if(!res.next()) {
			System.out.println("No Liked Playlists...");
			return likedPlaylists;
		}
		System.out.println("Liked Playlists");
		System.out.println("---------------------");
		do {
			likedPlaylists.add(res.getInt("PLAYLIST_ID"));
	        System.out.println(res.getString("PLAYLIST_NAME"));
	    } while(res.next());
		return likedPlaylists;
	}
	
	public boolean unlikeAPlaylist(int userId, int playlistId) throws SQLException {
		query = "UPDATE PlayList_Details SET IS_LIKED = 0 WHERE USER_ID = ? AND PLAYLIST_ID = ? AND IS_PRESENCE = 1";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		pstmt.setInt(2, playlistId);
		return pstmt.executeUpdate() > 0;
	}
	
	public boolean likeAPlayList(int playlistId) throws SQLException {
	    query = "UPDATE PlayList_Details SET IS_LIKED = ? WHERE PLAYLIST_ID = ?";
	    pstmt = conn.prepareStatement(query);
	    pstmt.setInt(1, 1);
	    pstmt.setInt(2, playlistId);
	    return pstmt.executeUpdate() > 0;
	}
}
