//$Id$
package com.music_player.api.song.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.json.JSONObject;

import com.music_player.api.artist.Artist;
import com.music_player.api.songqueue.util.SongQueueUtil;
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
	
	public Song getSongDetails(int songId, boolean isIncludeSongUrl) throws SQLException, IOException {
		query = "SELECT * "
				+ "FROM Song_Details JOIN Artist_Details "
				+ "ON Song_Details.Artist_ID = Artist_Details.ARTIST_ID " 
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
			
//			byte[] originalMp3Bytes = res.getBytes("SONG_FILE_MP3");
//            byte[] compressedMp3Bytes = compressBytes(originalMp3Bytes);
			Song.Builder songBuilder = new Song.Builder(res.getInt("SONG_ID"), res.getString("SONG_TITLE"))
					.duration(res.getString("DURATION"))
					.genre(res.getString("GENRE"))
					.artist(artist)
					.imageUrl(res.getString("IMAGE_URL"));
			 if(isIncludeSongUrl) {
				 return songBuilder.songUrl(res.getString("Song_URL")).build();
			 }
			 return songBuilder.build();
		}
		return null;
	}
	
	public Song getCurrentPlayingSong(int userId) throws SQLException {
		query = "SELECT * "
				+ "FROM Song_Details JOIN Artist_Details "
				+ "ON Song_Details.Artist_ID = Artist_Details.ARTIST_ID "
				+ "WHERE Song_Details.SONG_ID IN( "
				+ "SELECT SONG_ID FROM Queued_Songs WHERE USER_ID = ? AND IS_CURRENTPLAYING = 1)";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
		
		if(res.next()) {
			Artist artist = new Artist.Builder(res.getInt("ARTIST_ID"), res.getString("ARTIST_NAME"))
							.description(res.getString("DESCRIPTION"))
							.country(res.getString("COUNTRY"))
							.genre(res.getString("GENRE"))
							.build();
			
//			byte[] originalMp3Bytes = res.getBytes("SONG_FILE_MP3");
//            byte[] compressedMp3Bytes = compressBytes(originalMp3Bytes);
			Song.Builder songBuilder = new Song.Builder(res.getInt("SONG_ID"), res.getString("SONG_TITLE"))
					.duration(res.getString("DURATION"))
					.genre(res.getString("GENRE"))
					.artist(artist)
					.imageUrl(res.getString("IMAGE_URL"));
		    return songBuilder.songUrl(res.getString("Song_URL")).build();
			 
		}
		return null;
	}
	
	public List<Song> getAllSongs(boolean isIncludeSongUrl) throws SQLException {
		query = "SELECT * "
				+ "FROM Song_Details JOIN Artist_Details "
				+ "ON Song_Details.Artist_ID = Artist_Details.ARTIST_ID ";
		pstmt = conn.prepareStatement(query);
		res = pstmt.executeQuery();
		
		List<Song> songs = new ArrayList<>();
		while(res.next()) {
			Artist artist = new Artist.Builder(res.getInt("ARTIST_ID"), res.getString("ARTIST_NAME"))
							.description(res.getString("DESCRIPTION"))
							.country(res.getString("COUNTRY"))
							.genre(res.getString("GENRE"))
							.build();
			
			Song.Builder songBuilder = new Song.Builder(res.getInt("SONG_ID"), res.getString("SONG_TITLE"))
					.duration(res.getString("DURATION"))
					.genre(res.getString("GENRE"))
					.artist(artist)
					.imageUrl(res.getString("IMAGE_URL"));
			
			 if(isIncludeSongUrl) {
				 Song song = songBuilder.songUrl(res.getString("Song_URL")).build();
				 songs.add(song) ;
			 } else {
				 Song song = songBuilder.build();
				 songs.add(song);
			 }
		}
		return songs;
	}
	
	public List<Song> getFrequentlyPlayedSongs(int userId, boolean isCountNeeded) throws SQLException {
		List<Integer> recommendedSongs = new ArrayList<>();

		query = "SELECT Song_Details.*, Artist_Details.*, COUNT(Played_Song_History.SONG_ID) AS play_count "
				+ "FROM Played_Song_History "
				+ "JOIN Song_Details ON Played_Song_History.SONG_ID = Song_Details.SONG_ID "
				+ "JOIN Artist_Details ON Song_Details.Artist_ID = Artist_Details.ARTIST_ID "
				+ "WHERE Played_Song_History.USER_ID = ? "
				+ "GROUP BY Song_Details.SONG_ID "
				+ "ORDER BY play_count DESC "
				+ "LIMIT 5";
		
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
		
		List<Song> songs = new ArrayList<>();
		while(res.next()) {
			Artist artist = new Artist.Builder(res.getInt("ARTIST_ID"), res.getString("ARTIST_NAME"))
							.description(res.getString("DESCRIPTION"))
							.country(res.getString("COUNTRY"))
							.genre(res.getString("GENRE"))
							.build();
			
			Song.Builder songBuilder = new Song.Builder(res.getInt("SONG_ID"), res.getString("SONG_TITLE"))
					.duration(res.getString("DURATION"))
					.genre(res.getString("GENRE"))
					.artist(artist);
			
			 if(isCountNeeded) {
				 Song song = songBuilder.count(res.getInt("play_count")).build();
				 songs.add(song) ;
			 } else {
				 Song song = songBuilder.build();
				 songs.add(song);
			 }
		}
		return songs;
	}
	
	public List<Song> getSongs(boolean isIncludeSongUrl, String songIds) throws SQLException {
		query = "SELECT * "
				+ "FROM Song_Details JOIN Artist_Details "
				+ "ON Song_Details.Artist_ID = Artist_Details.ARTIST_ID "
				+ "WHERE SONG_ID IN (" + songIds + ")";
		pstmt = conn.prepareStatement(query);
		res = pstmt.executeQuery();
		
		List<Song> songs = new ArrayList<>();
		while(res.next()) {
			Artist artist = new Artist.Builder(res.getInt("ARTIST_ID"), res.getString("ARTIST_NAME"))
							.description(res.getString("DESCRIPTION"))
							.country(res.getString("COUNTRY"))
							.genre(res.getString("GENRE"))
							.build();
			
			Song.Builder songBuilder = new Song.Builder(res.getInt("SONG_ID"), res.getString("SONG_TITLE"))
					.duration(res.getString("DURATION"))
					.genre(res.getString("GENRE"))
					.artist(artist)
					.imageUrl(res.getString("IMAGE_URL"));
			
			 if(isIncludeSongUrl) {
				 Song song = songBuilder.songUrl(res.getString("Song_URL")).build();
				 songs.add(song) ;
			 } else {
				 Song song = songBuilder.build();
				 songs.add(song);
			 }
		}
		return songs;
	}
	
//	public byte[] getSongFile(int songId) throws SQLException {
//		query = "SELECT SONG_FILE_MP3 FROM Song_Details WHERE SONG_ID = ?";
//		pstmt = conn.prepareStatement(query);
//		pstmt.setInt(1, songId);
//		res = pstmt.executeQuery();
//		if(res.next()) {
//			return res.getBytes("SONG_FILE_MP3");
//		}
//		return null;
//	}
	
	private byte[] compressBytes(byte[] data) throws IOException {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream)) {
            gzipOutputStream.write(data);
            gzipOutputStream.close();
            return byteArrayOutputStream.toByteArray();
        }
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
	
	public boolean isSongInQueue(int userId, int songId, double order) throws Exception {
		query = "SELECT COUNT(*) AS ISSONGINQUEUE FROM Queued_Songs "
				+ "WHERE USER_ID = ? AND SONG_ID = ? AND `ORDER` = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		pstmt.setInt(2, songId);
		pstmt.setDouble(3, order);
		res = pstmt.executeQuery();
		if(res.next()) {
			return res.getBoolean("ISSONGINQUEUE");
		} else {
			throw new Exception();
		}
	}
	
	public String getSongLyrics(String songTitle, String artistName) {
		// api details - https://lyricsovh.docs.apiary.io/#reference/0/lyrics-of-a-song/search
		String lyricsAPI = "https://api.lyrics.ovh/v1/";
		String lyricsUrl = new StringBuilder(lyricsAPI)
							.append(artistName)
							.append("/")
							.append(songTitle)
							.toString();
		try {
			URL url = new URL(lyricsUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("GET");
	        
	        int responseCode = connection.getResponseCode();
	        if (responseCode == HttpURLConnection.HTTP_OK) {
	            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	            String inputLine;
	            StringBuilder response = new StringBuilder();

	            while ((inputLine = reader.readLine()) != null) {
	                response.append(inputLine);
	            }
	            reader.close();

	            JSONObject jsonResponse = new JSONObject(response.toString());
	            return jsonResponse.getString("lyrics");
//	            String lyrics = jsonResponse.getString("lyrics");
//	            System.out.println();
//	            System.out.println(lyrics);
	        } else {
	            System.out.println("Error: Unable to fetch lyrics. Response code: " + responseCode);
	        }
		} catch (Exception e) {
			System.out.println("Something went wrong while fetching song lyrics...");
		}
		return "Error loading lyrics";
	}
	
	public Song addAllSongsToQueue(int userId, boolean isQueueCleared) throws SQLException, NumberFormatException, IOException {
		double order;
		if(isQueueCleared) {
			order = 1;
		} else {
			order = SongQueueUtil.getInstance().getHighestOrder(userId) + 1;
		}
		
		List<Integer> songList = getAllSongIds();
		for (Integer songId : songList) {
	        SongQueueUtil.getInstance().addSongToQueue(userId, songId, order, -1);
	        order++; // Increment the order for the next song
	    }
		if(isQueueCleared) {
			return SongUtil.getInstance().getSongDetails(songList.get(0), true);
		} else {
			return null;
		}
	}
	
	public List<Integer> getAllSongIds() throws SQLException {
		query = "SELECT * "
				+ "FROM Song_Details JOIN Artist_Details "
				+ "ON Song_Details.Artist_ID = Artist_Details.ARTIST_ID ";
		pstmt = conn.prepareStatement(query);
		res = pstmt.executeQuery();
		
		List<Integer> songIds = new ArrayList<>();
		while(res.next()) {
			songIds.add(res.getInt("SONG_ID"));
		}
		return songIds;
	}
	
}
