//$Id$
package com.music_player.api.songqueue;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.music_player.api.song.util.Song;
import com.music_player.api.songqueue.util.SongQueueUtil;
import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.userpreference.util.UserPreferenceUtil;

public class SongQueueAPIImpl implements SongQueueAPI{
	
	private final Logger LOGGER = Logger.getLogger(SongQueueAPIImpl.class.getName());
	
	public static SongQueueAPIImpl getInstance() {
		return SongQueueAPIImplInstance.INSTANCE;
	}
	
	private static class SongQueueAPIImplInstance {
		private static final SongQueueAPIImpl INSTANCE = new SongQueueAPIImpl();
	}

	@Override
	public boolean clearQueue(int userId) {
		return SongQueueUtil.getInstance().clearSongQueue(userId);
	}
	
	@Override
	public Song moveToNextTrack(int userId) throws NumberFormatException, SQLException, IOException {
		Song nextSongDetails = SongQueueUtil.getInstance().getNextSongInQueue(userId, true);
		if(nextSongDetails == null) { // and if there is no loop...clear queue
			int currentUserSettings = UserPreferenceUtil.getInstance().getCurrentUserSettings(userId);
			boolean isAutoplayEnabled = SettingsMode.AUTOPLAY.isTypeMatch(currentUserSettings);
			boolean isLoopEnabled = SettingsMode.LOOP.isTypeMatch(currentUserSettings);
			if(isAutoplayEnabled) {
				// load songs from song details to queue and play
				// and set nextSongId in queue
				nextSongDetails = SongQueueUtil.getInstance().addAllSongsInQueue(userId, false);
			} else if(isLoopEnabled) {
				nextSongDetails = SongQueueUtil.getInstance().getFirstSongInQueue(userId, true);
			} else {
				// need to comment
				System.out.println("Player paused....");
//				SongQueue.getInstance().setPlaying(false);
				// clear queue if queue is over and there is no loop
				// handles for no looping
				SongQueueUtil.getInstance().clearSongQueue(userId);
				LOGGER.log(Level.WARNING, "No more song in queue...");
				return null;
			}
			
		}
		boolean isSuccess = false;
		if(nextSongDetails != null) {
			Song currentPlayingSong = SongQueueUtil.getInstance().getCurrentPlayingSong(userId);
			int currentPlayingSongId = currentPlayingSong.getSongId();
			isSuccess = SongQueueUtil.getInstance().resetCurrentPlayingSong(userId, currentPlayingSongId, nextSongDetails.getSongId(), nextSongDetails.getOrder());
		}
		
		if(isSuccess) {
			return nextSongDetails;
		}
		return null;
	}
	
	@Override
	public Song moveToPrevTrack(int userId) throws SQLException, IOException  {
		Song nextSongDetails = SongQueueUtil.getInstance().getPrevSongInQueue(userId, true);
		int currentUserSettings = UserPreferenceUtil.getInstance().getCurrentUserSettings(userId);
		boolean isLoopEnabled = SettingsMode.LOOP.isTypeMatch(currentUserSettings);
		if(nextSongDetails == null) {
			if (isLoopEnabled) {
				nextSongDetails = SongQueueUtil.getInstance().getLastSongInQueue(userId, true);
			} else {
				// repeat the same track
				double nextSongOrder = SongQueueUtil.getInstance().getCurrentPlayingSongOrder(userId);
				nextSongDetails = new Song.Builder(SongQueueUtil.getInstance().getCurrentPlayingSong(userId)).order(nextSongOrder).build();
			}
		}
		int currentPlayingSongId = SongQueueUtil.getInstance().getCurrentPlayingSong(userId).getSongId();
		boolean isSuccess = SongQueueUtil.getInstance().resetCurrentPlayingSong(userId, currentPlayingSongId, nextSongDetails.getSongId(), nextSongDetails.getOrder());
		if(isSuccess) {
			return nextSongDetails;	
		} 
		return null;
	}
	
	@Override
	public void shuffleSongs(int userId) throws SQLException {
		int currentUserSettings = UserPreferenceUtil.getInstance().getCurrentUserSettings(userId);
		boolean isShuffle = SettingsMode.SHUFFLE.isTypeMatch(currentUserSettings);
		if(isShuffle) {
			try {
				boolean isSuccess = false;
				List<Integer> allQueuedSongIds = SongQueueUtil.getInstance().getAllQueueSongs(userId);
				
				// Shuffle the song IDs
		        Collections.shuffle(allQueuedSongIds);
		        double orderToUpdate = SongQueueUtil.getInstance().getMaxOrderOfQueuedSongs(userId);
		        orderToUpdate +=1;
		        
		        isSuccess = SongQueueUtil.getInstance().updateQueuedSongOrders(userId, allQueuedSongIds, orderToUpdate);
		        // also update current playing song order
		        if(isSuccess) {
					System.out.println("Shuffled queued songs...");
		        } else {
					System.out.println("Failed to shuffle songs...");
		        }
			} catch (SQLException e) {		
				System.out.println("Failed to shuffle songs...");
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public void unShuffleSongs(int userId) throws SQLException {
		int currentUserSettings = UserPreferenceUtil.getInstance().getCurrentUserSettings(userId);
		boolean isShuffle = SettingsMode.SHUFFLE.isTypeMatch(currentUserSettings);
		if(!isShuffle) {
			try {
				boolean isSuccess = false;
				isSuccess = SongQueueUtil.getInstance().retainOriginalSongOrders(userId);
				if(isSuccess) {
					System.out.println("UnShuffled queued songs...");
		        } else {
					System.out.println("Failed to unshuffle songs...");
		        }
			} catch (Exception e) {
				System.out.println("Failed to unShuffle songs...");
				e.printStackTrace();
			}
		}
	}
}
