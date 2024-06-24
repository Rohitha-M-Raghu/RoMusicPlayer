//$Id$
package com.music_player.api.song;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.music_player.api.playlist.util.PlaylistUtil;
import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;
import com.music_player.api.songqueue.util.SongQueueUtil;
import com.music_player.api.userpreference.UserPreferenceAPIImpl;
import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.db.DBConnector;

public class SongAPIImpl implements SongAPI{
	
	private static final Logger LOGGER = Logger.getLogger(SongAPIImpl.class.getName());

	@Override
	public Song getSongDetails(int songId, boolean isIncludeSongUrl) throws SQLException, IOException {
		return SongUtil.getInstance().getSongDetails(songId, isIncludeSongUrl);
	}

	@Override
	public List<Song> getSongs(boolean isIncludeSongUrl) throws SQLException {
		return SongUtil.getInstance().getAllSongs(isIncludeSongUrl);
	}
	
	@Override
	public boolean playSong(int userId, int songId) {
		Connection conn = DBConnector.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			SongQueueUtil.getInstance().clearSongQueue(userId);
			SongQueueUtil.getInstance().addSongToQueue(userId, songId, 1.0, -1);
			SongQueueUtil.getInstance().setCurrentPlayingSong(userId, songId, 1.0);
			conn.commit();
			// shuffle off if on
			if(UserPreferenceAPIImpl.getInstance().getSettings(userId).isShuffle()) {
				UserPreferenceAPIImpl.getInstance().changeSettings(userId, SettingsMode.SHUFFLE, false);
			}
			
		} catch (Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				LOGGER.log(Level.WARNING,"Failed roll back transaction...");
			}
			LOGGER.log(Level.WARNING,"Failed to play song");
			return false;
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	@Override
	public boolean addSongToQueue(int userId, int songId) {
		// add song to end of queue
		try {
			double order = SongQueueUtil.getInstance().getHighestOrder(userId) + 1;
			SongQueueUtil.getInstance().addSongToQueue(userId, songId, order, -1);
			
			// shuffle off if on
			if(UserPreferenceAPIImpl.getInstance().getSettings(userId).isShuffle()) {
				UserPreferenceAPIImpl.getInstance().changeSettings(userId, SettingsMode.SHUFFLE, false);
			}
			return true;
		} catch (SQLException e) {
			return false;
		} 
	}
	
	@Override
	public boolean removeSongFromQueue(int userId, int songId, double order) throws SQLException {
		// display queued songs
		double currentPlayingSongOrder = SongQueueUtil.getInstance().getCurrentPlayingSongOrder(userId);
		boolean isDeleteCurrentPlayingSong = order == currentPlayingSongOrder;
		
		boolean isSuccess = SongQueueUtil.getInstance().removeSongFromQueue(userId, songId,order);
		if(isSuccess && isDeleteCurrentPlayingSong) {
//			SongQueue.getInstance().setSkipTrack(true);
			// handle for skip to next track
		} 
		return isSuccess;
	}
	
	@Override
	public boolean playSongNext(int userId, int songId) {
			
		double currentSongOrder = SongQueueUtil.getInstance().getCurrentPlayingSongOrder(userId);
		
		try {
			if(currentSongOrder == 0) {
				SongQueueUtil.getInstance().addSongToQueue(userId, songId, 1.0, -1);
				SongQueueUtil.getInstance().setCurrentPlayingSong(userId, songId, 1.0);
			} else {
				double songOrder;
				Song nextSongInQueue = SongQueueUtil.getInstance().getNextSongInQueue(userId, false);
				if(nextSongInQueue == null) {
					songOrder = currentSongOrder + 1;
				} else {
					double nextSongOrder = nextSongInQueue.getOrder();
					songOrder = (currentSongOrder + nextSongOrder) / 2;
				}
				
				SongQueueUtil.getInstance().addSongToQueue(userId, songId, songOrder, -1);
				LOGGER.log(Level.WARNING, "Song added");
				// shuffle off if on
				if(UserPreferenceAPIImpl.getInstance().getSettings(userId).isShuffle()) {
					UserPreferenceAPIImpl.getInstance().changeSettings(userId, SettingsMode.SHUFFLE, false);
				}
			}
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public String getSongLyrics(int songId) throws Exception {
		Song songDetails = SongUtil.getInstance().getSongDetails(songId, false);
		return SongUtil.getInstance().getSongLyrics(songDetails.getSongTitle(), songDetails.getArtist().getArtistName());
	}

	@Override
	public Song getCurrentPlatingSong(int userId) throws SQLException {
		return SongUtil.getInstance().getCurrentPlayingSong(userId);
	}

	@Override
	public Song playAllSongs(int userId) throws SQLException {
		Connection conn = DBConnector.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			SongQueueUtil.getInstance().clearSongQueue(userId);
			Song firstSong = SongUtil.getInstance().addAllSongsToQueue(userId, true);
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
