//$Id$
package com.music_player.api.userpreference;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.music_player.api.playlist.util.PlaylistUtil;
import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;
import com.music_player.api.songqueue.util.SongQueueUtil;
import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.userpreference.util.UserPreferenceUtil;
import com.music_player.api.userpreference.util.UserSettings;
import com.music_player.db.DBConnector;

public class UserPreferenceAPIImpl implements UserPreferenceAPI{
	
	private String errorMsg = "Something went wrong... Please try again...";
	public static UserPreferenceAPIImpl getInstance() {
		return UserPreferenceAPIImplInstance.INSTANCE;
	}
	
	private static class UserPreferenceAPIImplInstance {
		private static final UserPreferenceAPIImpl INSTANCE = new UserPreferenceAPIImpl();
	}
	
	@Override
	public boolean likeCurrentPlayingSong(int userId) throws Exception {
		Song currentPlayingSong = SongQueueUtil.getInstance().getCurrentPlayingSong(userId);
		return likeASong(userId, currentPlayingSong.getSongId());
	}
	
	@Override
	public boolean likeASong(int userId, int songId) throws Exception {
		boolean isSuccess = false;

		try {
			if(UserPreferenceUtil.getInstance().getLikedSongs(userId).contains(songId)) {
				System.out.println("Song is already liked...");
				return true;
			}
			return UserPreferenceUtil.getInstance().likeASong(userId, songId);
	
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	
	
	@Override
	public boolean unlikeASong(int userId, int songId) throws Exception {	
		return UserPreferenceUtil.getInstance().unlikeASong(userId, songId);
	}
	
	@Override
	public boolean unlikePlaylist(int userId, int playlistId) throws SQLException {
		return UserPreferenceUtil.getInstance().unlikeAPlaylist(userId, playlistId);
	}
	
	@Override
	public boolean likeAPlayList(int userId, int playlistId) throws SQLException {
		return UserPreferenceUtil.getInstance().likeAPlayList(playlistId);
	}
	
	@Override
	public void changeSettings(int userId, SettingsMode setting, boolean newSettings) {
		int currentSetting = -1;
			try {
				currentSetting = UserPreferenceUtil.getInstance().getCurrentUserSettings(userId);
			
				if(currentSetting != -1) {
					boolean isCurrentlyActive = setting.isTypeMatch(currentSetting);
					if(newSettings != isCurrentlyActive) {
						int valueToUpdate;
						if(newSettings) {
							valueToUpdate = currentSetting + setting.getMode();
						} else {
							valueToUpdate = currentSetting - setting.getMode();
						}
						boolean isValueUpdated = UserPreferenceUtil.getInstance().updateSettings(userId, valueToUpdate);
						if(isValueUpdated) {
							System.out.println(setting + " is turned " + (newSettings?"ON":"OFF"));
							if(setting.equals(SettingsMode.AUTOPLAY) && !newSettings) {
								SongQueueUtil.getInstance().clearSongQueue(userId);
							} else if(setting.equals(SettingsMode.SHUFFLE)) {
								if(newSettings) {
									shuffleSongs(userId);
								} else {
									unShuffleSongs(userId);
								}
							}
						}
					} else {
							System.out.println(setting + " is already " + (newSettings?"ON":"OFF"));
					}
				} else {
					System.out.println("Issue while retreiving data from DB...");
				}
			} catch (SQLException e) {
				System.out.println(errorMsg);
			}
		}
	
	@Override
	public List<Song> getLikedSongs(int userId) throws SQLException {
		List<Integer> likedSongIds = UserPreferenceUtil.getInstance().getLikedSongs(userId);
		if(likedSongIds == null || likedSongIds.isEmpty()) {
			return new ArrayList<>();
		} else {
			StringBuilder songs = new StringBuilder();
			for(int i = 0; i < likedSongIds.size(); ++i) {
				songs.append(likedSongIds.get(i));
				if(i < likedSongIds.size() - 1) {
					songs.append(",");
				}
			}
			return SongUtil.getInstance().getSongs(true, songs.toString());
		}
	}
	
	@Override
	public void getLikedPlayLists(int userId) {
		try {
			UserPreferenceUtil.getInstance().getLikedPlayLists(userId);
		} catch (Exception e) {
			System.out.println(errorMsg);
		}
	}
	
	@Override
	public void displayFrequentlyPlayedGenre() {
//		try {
//			MusicPlayerDBAPIImpl.getInstance().displayFrequentlyPlayedGenre();
//		} catch (Exception e) {
//			System.out.println(errorMsg);
//		}
	}
	
	@Override 
	public List<Song> getFrequentlyPlayedSongs(int userId, boolean isCountNeeded) throws SQLException {
		return SongUtil.getInstance().getFrequentlyPlayedSongs(userId, isCountNeeded);
	}
	
	@Override 
	public void displayMusicRecommendation() {
//		try {
//			MusicPlayerDBAPIImpl.getInstance().displayMusicRecommendation();
//		} catch (Exception e) {
//			System.out.println(errorMsg);
//		}
	}
	
	
	@Override
	public void changeUserTheme() {
//		boolean isSuccess = false;
//		Scanner inputReader = new Scanner(System.in);
//		for(DisplayColor color: DisplayColor.values()) {
//			MusicPlayerMainMenu.getInstance().printHighlightMessage(color.name(), color);
//		}
//		System.out.println("Selected theme: ");
//		String colorInput = inputReader.next().toUpperCase();
//		 try {
//	        DisplayColor selectedColor = DisplayColor.valueOf(colorInput);
//	        if(selectedColor.name().equals(Cache.getInstance().getDataFromCache("theme"))) {
//	        	System.out.println("The theme is already " + selectedColor.name() + "...");
//	        	return;
//	        }
//	        isSuccess = MusicPlayerDBAPIImpl.getInstance().setUserTheme(selectedColor.name());
//	        if(isSuccess) {
//	        	Cache.getInstance().cacheData("theme", selectedColor.name());
//	        	MusicPlayerMainMenu.getInstance().printHighlightMessage("Theme Changed Successfully", selectedColor);
//	        } else {
//	        	System.out.println("Failed to change theme...");
//	        }
//	    } catch (IllegalArgumentException e) {
//	        System.out.println("Invalid color. Please enter a valid color.");
//	    }
	}
	
	@Override
	public UserSettings getSettings(int userId) throws SQLException {
		int currentUserSettings = UserPreferenceUtil.getInstance().getCurrentUserSettings(userId);
		boolean isShuffle = SettingsMode.SHUFFLE.isTypeMatch(currentUserSettings);
		boolean isLoop = SettingsMode.LOOP.isTypeMatch(currentUserSettings);
		boolean isAutoplay = SettingsMode.AUTOPLAY.isTypeMatch(currentUserSettings);
		return new UserSettings.Builder()
				.isAutoplay(isAutoplay)
				.isLoop(isLoop)
				.isShuffle(isShuffle)
				.build();
	}
	
	private void shuffleSongs(int userId) throws SQLException {
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
	
	private void unShuffleSongs(int userId) throws SQLException {
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
	
	private void changePassword() {
//		Scanner inputReader = new Scanner(System.in);
//		System.out.println("Enter New Password: ");
//		String newPassword = inputReader.nextLine();
//		try {
//			if(MusicPlayerDBAPIImpl.getInstance().passwordValidation()) {
//				if(MusicPlayerDBAPIImpl.getInstance().changePassword(newPassword)) {
//					System.out.println("Password changed Successfully...");
//				}
//			} else {
//				System.out.println("Invalid Password...");
//			}
//		} catch (SQLException e) {
//			System.out.println("Something went wrong...");
//			e.printStackTrace();
//		}
	}

	@Override
	public Song playlikedSongs(int userId) throws SQLException {
		Connection conn = DBConnector.getInstance().getConnection();
		try {
			conn.setAutoCommit(false);
			SongQueueUtil.getInstance().clearSongQueue(userId);
			Song firstSong = UserPreferenceUtil.getInstance().addLikedSongsToQueue(userId, true);
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