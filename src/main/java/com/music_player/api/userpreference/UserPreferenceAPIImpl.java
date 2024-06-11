//$Id$
package com.music_player.api.userpreference;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;
import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.userpreference.util.UserPreferenceUtil;
import com.music_player.api.userpreference.util.UserSettings;
import com.music_player.queue.SongQueue;
import com.music_player.queue.SongQueueAPIImpl;
import com.music_player.queue.SongQueueUtil;

public class UserPreferenceAPIImpl implements UserPreferenceAPI{
	
	private String errorMsg = "Something went wrong... Please try again...";
	public static UserPreferenceAPIImpl getInstance() {
		return UserPreferenceAPIImplInstance.INSTANCE;
	}
	
	private static class UserPreferenceAPIImplInstance {
		private static final UserPreferenceAPIImpl INSTANCE = new UserPreferenceAPIImpl();
	}
	
	@Override
	public void likeCurrentPlayingSong() {
		try {
			Song currentPlayingSong = SongQueueUtil.getInstance().getCurrentPlayingSong();
			
			String songTitle = currentPlayingSong.getSongTitle();
			String artistName = currentPlayingSong.getArtist().getArtistName();
//			likeASong(songTitle, artistName);
			// implement
		} catch (Exception e) {
			System.out.println(errorMsg);
		}
	}
	
	@Override
	public void likeASong(int userId, int songId) throws Exception {
		boolean isSuccess = false;

		try {
			if(!SongUtil.getInstance().checkIfSongExists(songId)) {
				throw new NullPointerException("Song not found...");
			}
			if(UserPreferenceUtil.getInstance().getLikedSongs(userId).contains(songId)) {
				System.out.println("Song is already liked...");
				return;
			}
			isSuccess = UserPreferenceUtil.getInstance().likeASong(userId, songId);
	
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if(isSuccess) {
			System.out.println("Song liked...");
		} else {
			System.out.println("Song not found.");
		}
	}
	
	@Override
	public void unlikeASong(int userId, int songId) throws Exception {
		// display liked songs
		boolean isSuccess = false;
		List<Integer> likedSongIds = new ArrayList<>();
		try {
			likedSongIds = UserPreferenceUtil.getInstance().getLikedSongs(userId);
		} catch (SQLException e) {
			System.out.println("Issue while fetching liked songs...");
			return;
		}
		if (likedSongIds.isEmpty()) {
			return;
		}
		try {
			if(!SongUtil.getInstance().checkIfSongExists(songId)) {
				throw new NullPointerException("Song not found...");
			}
			if(likedSongIds.contains(songId)) {
				// unlike song
				isSuccess = UserPreferenceUtil.getInstance().unlikeASong(userId, songId);
			} else {
				System.out.println("Song is not a liked song...");
				return;
			}
		} catch (SQLException e) {
			System.out.println("Something went wrong while unliking song...");
		}
		if(isSuccess) {
			System.out.println("Song is removed from liked songs..");
		} else {
			System.out.println("Failed to remove song from liked songs...");
		}
	}
	
	@Override
	public void unlikePlaylist(int userId, int playlistId) {
		// display liked playlists
		boolean isSuccess = false;
		List<Integer> likedPlaylists = new ArrayList<>();
		try {
			likedPlaylists = UserPreferenceUtil.getInstance().getLikedPlayLists(userId);
		} catch (SQLException e) {
			System.out.println("Issue while fetching liked playlists...");
			return;
		}
		if (likedPlaylists.isEmpty()) {
			return;
		}
		
		try {
			if(playlistId == -1) {
				throw new NullPointerException("Playlist not found...");
			}
			if(likedPlaylists.contains(playlistId)) {
				isSuccess = UserPreferenceUtil.getInstance().unlikeAPlaylist(userId, playlistId);
			} else {
				System.out.println("Not a liked playlist...");
				return;
			}
		} catch (SQLException e) {
			System.out.println("Something went wrong while unliking playlist...");
		}
		if(isSuccess) {
			System.out.println("Playlist is removed from liked playlists..");
		} else {
			System.out.println("Failed to remove playlist from liked playlists...");
		}
	}
	
	@Override
	public void likeAPlayList(int userId, int playlistId) {
		boolean isSuccess = false;
		try {
			if(playlistId == -1) {
				throw new NullPointerException("Playlist Not Found...");
			}
			if(UserPreferenceUtil.getInstance().getLikedPlayLists(userId).contains(playlistId)) {
				System.out.println("Already liked...");
				return;
			} 
			isSuccess = UserPreferenceUtil.getInstance().likeAPlayList(playlistId);

		} catch (SQLException e) {
			System.out.println(errorMsg);
			e.printStackTrace();
		}
		
		if(isSuccess) {
			System.out.println("liked...");
		} else {
			System.out.println("Failed to like Playlist...");
		}
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
								SongQueue.getInstance().setPlaying(true);
							} else if(setting.equals(SettingsMode.SHUFFLE)) {
								if(newSettings) {
									// implements
//									SongQueueAPIImpl.getInstance().shuffleSongs();
								} else {
									// implement
//									SongQueueAPIImpl.getInstance().unShuffleSongs();
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
	public void getLikedSongs(int userId) {
		try {
			UserPreferenceUtil.getInstance().getLikedSongs(userId);
		} catch (Exception e) {
			System.out.println(errorMsg);
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
	public void displayFrequentlyPlayedSongs() {
//		try {
//			MusicPlayerDBAPIImpl.getInstance().displayFrequentlyPlayedSongs(true, true);
//		} catch (Exception e) {
//			System.out.println(errorMsg);
//		}
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
}