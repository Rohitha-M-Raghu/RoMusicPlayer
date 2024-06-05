//$Id$
package com.music_player.api.song.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.music_player.api.artist.Artist;
import com.music_player.db.DBConnector;

public class SongUtil {
	
	private final Connection conn;
	private String query;
	private PreparedStatement pstmt;
	private ResultSet res;
	
	public static SongUtil getInstance() {
		return SongUtilInstance.INSTANCE;
	}
	
	private static class SongUtilInstance {
		private static final SongUtil INSTANCE = new SongUtil();
	}
	
	private SongUtil() {
		this.conn = DBConnector.getInstance().getConnection();
	}
	
	public Song getSongDetails(int songId) throws SQLException {
		query = "SELECT * "
				+ "FROM Song_Details JOIN Artist_Details "
				+ "ON Song_Details.Artist_ID = Artist_Details.ARTIST_ID" 
				+ "WHERE Song_Details.SONG_ID = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, songId);
		res = pstmt.executeQuery();
		
		if(res.next()) {
			Artist artist = new Artist.Builder(res.getInt("ARTIST_ID"), res.getString("ARTIST_NAME"))
							.description(res.getString("DESCRIPTION"))
							.country(res.getString("COUNTRY"))
							.genre(res.getString("GENRE"))
							.build();
			
			return new Song.Builder(res.getInt("SONG_ID"), res.getString("SONG_TITLE"))
						.duration(res.getString("DURATION"))
						.genre(res.getString("GENRE"))
						.artist(artist)
						.mp3File(res.getBytes("SONG_FILE_MP3"))
						.build();
		}
		return null;
	}
	
	public boolean checkIfSongExists(int SongId) throws Exception {
		query = "SELECT COUNT(*) AS ISSONGEXIST FROM Song_Details " 
				+ "WHERE SONG_ID = ?";
		
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, SongId);
		res = pstmt.executeQuery();
		if(res.next()) {
			return res.getBoolean("ISSONGEXIST");
		} else {
			throw new Exception();
		}
	}
}
