//$Id$
package com.music_player.api.songqueue.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;
import com.music_player.api.userpreference.util.UserPreferenceUtil;
import com.music_player.db.DBConnector;

public class SongQueueUtil {
	
	private final Connection conn;
	private String query;
	private PreparedStatement pstmt;
	private ResultSet res;
	
	private static final Logger LOGGER = Logger.getLogger(SongQueueUtil.class.getName());
	
	public static SongQueueUtil getInstance() {
		return SongQueueUtilInstance.INSTANCE;
	}
	
	private static class SongQueueUtilInstance {
		private static final SongQueueUtil INSTANCE = new SongQueueUtil();
	}
	
	private SongQueueUtil() {
		this.conn = DBConnector.getInstance().getConnection();
	}
	
	public Song moveToNextTrack(int userId) throws NumberFormatException, SQLException, IOException {
		Song nextSongDetails = getNextSongInQueue(userId, false);
		if(nextSongDetails == null) { // and if there is no loop...clear queue
			int currentUserSettings = UserPreferenceUtil.getInstance().getCurrentUserSettings(userId);
			boolean isAutoplayEnabled = SettingsMode.AUTOPLAY.isTypeMatch(currentUserSettings);
			boolean isLoopEnabled = SettingsMode.LOOP.isTypeMatch(currentUserSettings);
			if(isAutoplayEnabled) {
				// load songs from song details to queue and play
				// and set nextSongId in queue
				nextSongDetails = addAllSongsInQueue(userId, false);
			} else if(isLoopEnabled) {
				nextSongDetails = getFirstSongInQueue(userId, false);
			} else {
				// need to comment
				System.out.println("Player paused....");
//				SongQueue.getInstance().setPlaying(false);
				// clear queue if queue is over and there is no loop
				// handles for no looping
				clearSongQueue(userId);
				LOGGER.log(Level.WARNING, "No more song in queue...");
				return null;
			}
			
		}
		boolean isSuccess = false;
		if(nextSongDetails != null) {
			isSuccess = resetCurrentPlayingSong(userId, getCurrentPlayingSong(userId).getSongId(), nextSongDetails.getSongId(), nextSongDetails.getOrder());
		}
		
		if(isSuccess) {
			return nextSongDetails;
		}
		return null;
	}
	
	public Song moveToPrevTrack(int userId) throws SQLException, IOException  {
		Song nextSongDetails = getPrevSongInQueue(userId, false);
		int currentUserSettings = UserPreferenceUtil.getInstance().getCurrentUserSettings(userId);
		boolean isLoopEnabled = SettingsMode.LOOP.isTypeMatch(currentUserSettings);
		if (isLoopEnabled) {
			nextSongDetails = getLastSongInQueue(userId, false);
		} else if(nextSongDetails == null) {
			// repeat the same track
			
			double nextSongOrder = SongQueueUtil.getInstance().getCurrentPlayingSongOrder(userId);
			nextSongDetails = new Song.Builder(getCurrentPlayingSong(userId)).order(nextSongOrder).build();
			
		}
		
		int currentPlayingSongId = SongQueueUtil.getInstance().getCurrentPlayingSong(userId).getSongId();
		boolean isSuccess = resetCurrentPlayingSong(userId, currentPlayingSongId, nextSongDetails.getSongId(), nextSongDetails.getOrder());
		if(isSuccess) {
			return nextSongDetails;	
		} 
		return null;
	}
	
	public Song getPrevSongInQueue(int userId, boolean isSongDetailsNeeded) throws SQLException, IOException {
		double currentPlayingSongOrder = getCurrentPlayingSongOrder(userId);
		query = "SELECT * FROM Queued_Songs WHERE USER_ID = ? AND `ORDER` < ? ORDER BY `ORDER` DESC LIMIT 1";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		pstmt.setDouble(2, currentPlayingSongOrder);
		res = pstmt.executeQuery();
		if(res.next()) {
			 
			if(!isSongDetailsNeeded) {
				return new Song.Builder(res.getInt("SONG_ID"), res.getDouble("ORDER")).build();
			}
			Song nextSongDetails = SongUtil.getInstance().getSongDetails(res.getInt("SONG_ID"), true);
			return new Song.Builder(nextSongDetails).order(res.getDouble("ORDER")).build();
		}
		return null;
	}
	
	public Song getNextSongInQueue(int userId, boolean isSongDetailsNeeded) throws NumberFormatException, SQLException, IOException {
		// check whether we need to keep this outside
		double currentPlayingSongOrder = SongQueueUtil.getInstance().getCurrentPlayingSongOrder(userId);
		query = "SELECT * FROM Queued_Songs WHERE USER_ID = ? AND `ORDER` > ? ORDER BY `ORDER` LIMIT 1";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		pstmt.setDouble(2, currentPlayingSongOrder);
		res = pstmt.executeQuery();
		if(res.next()) {
			if(!isSongDetailsNeeded) {
				return new Song.Builder(res.getInt("SONG_ID"), res.getDouble("ORDER")).build();
			}
			Song nextSongDetails = SongUtil.getInstance().getSongDetails(res.getInt("SONG_ID"), true);
			return new Song.Builder(nextSongDetails).order(res.getDouble("ORDER")).build();
		}
		return null;
	}
	
	public Song getLastSongInQueue(int userId, boolean isSongDetailsNeeded) throws SQLException, IOException {
		query = "SELECT * FROM Queued_Songs WHERE USER_ID = ? ORDER BY `ORDER` DESC LIMIT 1";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
		if(res.next()) {
			Song nextSongDetails = new Song.Builder(res.getInt("SONG_ID"), res.getDouble("ORDER")).build();

			if(!isSongDetailsNeeded) {
				return nextSongDetails;
			} 
			return new Song.Builder(SongUtil.getInstance().getSongDetails(res.getInt("SONG_ID"), true))
					.order(res.getDouble("ORDER")).build();
		}
		return null;
	} 
	
	
	public Song getCurrentPlayingSong(int userId) throws SQLException {
		query = "SELECT * FROM Song_Details WHERE " 
				+ "SONG_ID IN " 
				+ "(SELECT SONG_ID  FROM Queued_Songs WHERE IS_CURRENTPLAYING = 1 AND USER_ID = ?)";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
		if(res.next()) {
			return new Song.Builder(res.getInt("SONG_ID"), res.getString("SONG_TITLE"))
					.duration(res.getTime("DURATION").toString())
					.build();	
		}
		return null;
	}
	
	public boolean resetCurrentPlayingSong(int userId, int currentSongId, int nextSongId, double order) throws SQLException {
		boolean isResetCurrentPlayingSong = false;
		
		isResetCurrentPlayingSong = resetCurrentPlayingSong(userId);	
		
		if(isResetCurrentPlayingSong) {
			query = "UPDATE Queued_Songs SET IS_CURRENTPLAYING = 1 WHERE SONG_ID = ? AND `ORDER` = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, nextSongId);
			pstmt.setDouble(2, order);
			if(pstmt.executeUpdate() > 0) {
				return true;
			}
		}
		return false;
	}
	
	public boolean resetCurrentPlayingSong(int userId) {
		try {
			query = "UPDATE Queued_Songs SET IS_CURRENTPLAYING = 0 WHERE IS_CURRENTPLAYING = 1 AND USER_ID = ?";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userId);
			pstmt.executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void setCurrentPlayingSong(int userId, int songId, double order) throws SQLException {
		query = "UPDATE Queued_Songs SET IS_CURRENTPLAYING = 1 WHERE USER_ID = ? AND SONG_ID = ? AND `ORDER` = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		pstmt.setInt(2, songId);
		pstmt.setDouble(3, order);
		pstmt.executeUpdate();
	}
	
	public Song addAllSongsInQueue(int userId, boolean isQueueCleared) throws SQLException, IOException {
		double order;
		double nextSongOrder = 1;
		List<Integer> songList;
		if(isQueueCleared) {
			order = getCurrentPlayingSongOrder(userId) + 1;
		} else {
			order = getHighestOrder(userId) + 1;
			nextSongOrder = order;
		}
		songList = getAllSongs();
		for (Integer songId : songList) {
	        addSongToQueue(userId, songId, order, -1);
	        order++; 
	    }
		if(songList == null || songList.isEmpty()) {
			System.out.println("No Songs to add...");
			return null;
		}
		if(isQueueCleared) {
			return getFirstSongInQueue(userId, false);
		} else {
			Song nextSongToPlay = SongUtil.getInstance().getSongDetails(songList.get(0).intValue(), false);
			return new Song.Builder(nextSongToPlay).order(nextSongOrder).build();
		}
	}
	
	public double getHighestOrder(int userId) throws SQLException {
		query = "SELECT MAX(`ORDER`) AS highest_order FROM Queued_Songs WHERE USER_ID = ?";
		pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, userId);
        res = pstmt.executeQuery();
        if (res.next()) {
            return res.getDouble("highest_order");
        }
        return 0;
	}
	
	public List<Integer> getAllSongs() {
		List<Integer> songList = new ArrayList<>();
		try {
			query = "SELECT * FROM Song_Details";
			pstmt = conn.prepareStatement(query);
			res = pstmt.executeQuery();
			if(!res.next()) {
				return songList;
			} 
			do {
				songList.add(res.getInt("SONG_ID"));
			}while(res.next());
		} catch (Exception e) {
			return songList;
		}
		return songList;
	}
	
	public void addSongToQueue(int userId, int songId, double order, int playListId) throws SQLException {
	    query = "INSERT INTO Queued_Songs (USER_ID, SONG_ID, CREATED_TIME, `ORDER`, PLAYLIST_ID) VALUES (?, ?, ?, ?, ?)";
	    if(playListId == -1) {
	    	query = "INSERT INTO Queued_Songs (USER_ID, SONG_ID, CREATED_TIME, `ORDER`) VALUES (?, ?, ?, ?)";
	    }
	    pstmt = conn.prepareStatement(query);
	    pstmt.setInt(1, userId);
	    pstmt.setInt(2, songId);
	    pstmt.setLong(3, System.currentTimeMillis());
	    pstmt.setDouble(4, order);
	    if(playListId != -1) {
	    	pstmt.setInt(5, playListId);
	    }
	    pstmt.executeUpdate();
	    pstmt.close();
	}
	
	public Song getFirstSongInQueue(int userId, boolean isSongDetailsNeeded) throws NumberFormatException, SQLException, IOException {
		query = "SELECT * FROM Queued_Songs WHERE USER_ID = ? ORDER BY `ORDER` LIMIT 1";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
		if(res.next()) {
			if(isSongDetailsNeeded) {
				return SongUtil.getInstance().getSongDetails(res.getInt("SONG_ID"), true);
			} else {
				return new Song.Builder(res.getInt("SONG_ID"), res.getDouble("ORDER")).build();
			}
		}
		return null;
	}

	public boolean clearSongQueue(int userId) {
		query = "DELETE FROM Queued_Songs WHERE USER_ID = ?";
        try {
        	pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
			return false;
		}
	}
	
	public List<Double> displayQueuedSongs(int userId) {
		try {
			query = "SELECT Song_Details.SONG_TITLE, Queued_Songs.`ORDER` " +
                    "FROM Queued_Songs " +
                    "JOIN Song_Details ON Queued_Songs.SONG_ID = Song_Details.SONG_ID " +
                    "WHERE Queued_Songs.USER_ID = ? " +
                    "ORDER BY Queued_Songs.ORDER";
			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, userId);
			res = pstmt.executeQuery();
			if(res == null) {
				System.out.println("No Songs in queue...");
				return new ArrayList<>();
			}
			List<Double> orderList = new ArrayList<>();
			System.out.println("SongQueue");
			System.out.println("---------------------");
			int songCount = 0;
			while(res.next()) {
				System.out.println(++songCount + ". " + res.getString("SONG_TITLE"));
				orderList.add(res.getDouble("ORDER"));
			}
			return orderList;
		} catch (Exception e) {
			return new ArrayList<>();
		}
	}
	
	public double getCurrentPlayingSongOrder(int userId) {
		query = "SELECT * FROM Queued_Songs WHERE USER_ID = ? AND IS_CURRENTPLAYING = 1";
		try {
			pstmt = conn.prepareStatement(query);
		    pstmt.setInt(1, userId);
		    res = pstmt.executeQuery();
		    if (res.next()) {
		        return res.getDouble("ORDER");
		    }
		} catch (Exception e) {
			return -1;
		}
		return -1;
	}
	
	public boolean removeSongFromQueue(int userId, int songId, Double order) throws SQLException {
		if(order < 0) {
			return false;
		}
		query = "DELETE FROM Queued_Songs WHERE USER_ID = ? AND SONG_ID = ? AND `ORDER` = ?";
        
    	pstmt = conn.prepareStatement(query);
        pstmt.setInt(1, userId);
        pstmt.setInt(2, songId);
        pstmt.setDouble(3, order);
        return pstmt.executeUpdate() > 0;
        
	}
	
	public List<Integer> getAllQueueSongs(int userId) throws SQLException {
		query = "SELECT SONG_ID FROM Queued_Songs WHERE USER_ID = ?";
		List<Integer> songIds = new ArrayList<>();
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
		while(res.next()) {
			songIds.add(res.getInt("SONG_ID"));
		}
		return songIds;
	}
	
	public int getMaxOrderOfQueuedSongs(int userId) throws SQLException {
		query = "SELECT MAX(`ORDER`) AS highest_order FROM Queued_Songs WHERE USER_ID = ?";
		pstmt = conn.prepareStatement(query);
		pstmt.setInt(1, userId);
		res = pstmt.executeQuery();
        if (res.next()) {
            return res.getInt("highest_order");
        }
        return 0;
	}
	
	public boolean updateQueuedSongOrders(int userId, List<Integer> songIds, double order) throws SQLException {
		query = "UPDATE Queued_Songs SET `ORDER` = ?, ORIGINAL_ORDER = ? WHERE SONG_ID = ? AND USER_ID = ?";
        String querySelect = "SELECT `ORDER` FROM Queued_Songs WHERE SONG_ID = ? AND USER_ID = ?";
        try(PreparedStatement pstmtSelect = conn.prepareStatement(querySelect)) {
        	pstmt = conn.prepareStatement(query);
    		int currentPlayingSongId = getCurrentPlayingSong(userId).getSongId();
    		
    		for(int songId: songIds) {
    			pstmtSelect.setInt(1, songId);
                pstmtSelect.setInt(2, userId);
                res = pstmtSelect.executeQuery();
                if (res.next()) {
                    double originalOrder = res.getDouble("ORDER");
                    if(songId == currentPlayingSongId) {
        				pstmt.setDouble(1, originalOrder);     
        			} else {
        				 pstmt.setDouble(1, order++);
        			}
                    pstmt.setDouble(2, originalOrder);
        			pstmt.setInt(3, songId);
        			pstmt.setInt(4, userId);
        			pstmt.addBatch();
                }
    		}
    		int[] updateCounts = pstmt.executeBatch();
            for (int count : updateCounts) {
                if (count == Statement.EXECUTE_FAILED) {
                    return false;
                }
            }
        }
		
        return true;
    }
	
	public boolean retainOriginalSongOrders(int userId) throws SQLException {
	    String updateQuery = "UPDATE Queued_Songs SET `ORDER` = ? WHERE SONG_ID = ? AND USER_ID = ?";
	    try (PreparedStatement updateStatement = conn.prepareStatement(updateQuery)) {
	        String selectQuery = "SELECT SONG_ID, ORIGINAL_ORDER FROM Queued_Songs WHERE USER_ID = ? AND ORIGINAL_ORDER IS NOT NULL";
	        try (PreparedStatement selectStatement = conn.prepareStatement(selectQuery)) {
	            selectStatement.setInt(1, userId);
	            try (ResultSet rs = selectStatement.executeQuery()) {
	                while (rs.next()) {
	                    double order = rs.getDouble("ORIGINAL_ORDER");
	                    int songId = rs.getInt("SONG_ID");
	                    updateStatement.setDouble(1, order);
	                    updateStatement.setInt(2, songId);
	                    updateStatement.setInt(3, userId);
	                    updateStatement.addBatch();
	                }
	            }
	        }

	        int[] updateCounts = updateStatement.executeBatch();
	        for (int count : updateCounts) {
	            if (count == Statement.EXECUTE_FAILED) {
	                return false;
	            }
	        }
	        return true;
	    }
	}
}