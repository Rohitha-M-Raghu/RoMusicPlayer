//$Id$
package com.music_player.queue;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.userpreference.util.UserPreferenceUtil;

public class SongQueueAPIImpl implements SongQueueAPI{
	
//    private static final String LYRICS_API_URL = "https://api.lyrics.ovh/v1/";
	
	public static SongQueueAPIImpl getInstance() {
		return SongQueueAPIImplInstance.INSTANCE;
	}
	
	private static class SongQueueAPIImplInstance {
		private static final SongQueueAPIImpl INSTANCE = new SongQueueAPIImpl();
	}

	@Override
	public void clearQueue(int userId) {
		SongQueueUtil.getInstance().clearSongQueue(userId);
		SongQueue.getInstance().setQueueEmptied(true);
		SongQueue.getInstance().setCurrentPlayingSongId(0);
		SongQueue.getInstance().setCurrentPlayingSongOrder(0);
		System.out.println("Queue cleared...");
	}
	
	@Override
	public boolean removeSongFromQueue(int userId) {
		// display queued songs
		Scanner inputReader = new Scanner(System.in);
		List<Double> orderList = SongQueueUtil.getInstance().displayQueuedSongs(userId);
		System.out.println("Enter song to be deleted: ");
		double order = -1;
		try {
			order = orderList.get(inputReader.nextInt() -1);
		} catch (Exception e) {
			System.err.println("Song not found");
			return false;
		}
		double currentPlayingSongOrder = SongQueueUtil.getInstance().getCurrentPlayingSongOrder(userId);
		boolean isDeleteCurrentPlayingSong = order == currentPlayingSongOrder;
		
		boolean isSuccess = SongQueueUtil.getInstance().removeSongFromQueue(userId, order);
		if(isSuccess) {
			if(isDeleteCurrentPlayingSong) {
				SongQueue.getInstance().setSkipTrack(true);
			}
			System.out.println("Song removed from Queue...");
		} else {
			System.out.println("Failed to remove song...");
		}
		return isDeleteCurrentPlayingSong;
		// return orders in arraylist
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
		        SongQueue.getInstance().setCurrentPlayingSongOrder(SongQueueUtil.getInstance().getCurrentPlayingSongOrder(userId));
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
				SongQueue.getInstance().setCurrentPlayingSongOrder(SongQueueUtil.getInstance().getCurrentPlayingSongOrder(userId));
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
	
//	@Override
//	public void displayStatistics() {
//		
//		Song song = MusicPlayerDBAPIImpl.getInstance().getSongOfTheDay();
//		MusicPlayerMainMenu.getInstance().printHighlightMessage(getSongData("Song of the day", song), DisplayColor.YELLOW);
//		
//		// total duration streamed
//		song = MusicPlayerDBAPIImpl.getInstance().mostPlayedSongInApp();
//		MusicPlayerMainMenu.getInstance().printHighlightMessage(getSongData("Most Streamed Song", song), DisplayColor.MAGENTA);
//		
//		// total duration streamed
//		song = MusicPlayerDBAPIImpl.getInstance().mostStreamedArtistInApp();
//		MusicPlayerMainMenu.getInstance().printHighlightMessage(getSongData("Most Streamed Artist", song), DisplayColor.YELLOW);
//	}
	
//	@Override
//	public void displaySongLyrics() {
//		Scanner inputReader = new Scanner(System.in);
//		System.out.println("Enter songTitle: ");
//		String songTitle = inputReader.nextLine();
//		System.out.println("Enter ArtistName: ");
//		String artistName = inputReader.nextLine();
//		
//		try {
//			Song song = MusicPlayerDBAPIImpl.getInstance().getSongDetails(songTitle, artistName);
//			if(song == null) {
//				throw new PlaylistOrSongNotFoundException("Song not found...");
//			}			
//			displaySongLyrics(songTitle, artistName);
//		} catch (SQLException e) {
//			e.printStackTrace();
//			System.out.println("Something went wrong... Please try again...");
//		} catch (PlaylistOrSongNotFoundException e) {
//			System.out.println(e.getMessage());
//		}
//	}
	
//	@Override
//	public void displaySongLyrics(String songTitle, String artistName) {
//		// api details - https://lyricsovh.docs.apiary.io/#reference/0/lyrics-of-a-song/search
//		String lyricsUrl = new StringBuilder(LYRICS_API_URL)
//							.append(artistName)
//							.append("/")
//							.append(songTitle)
//							.toString();
//		try {
//			URL url = new URL(lyricsUrl);
//	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//	        connection.setRequestMethod("GET");
//	        
//	        int responseCode = connection.getResponseCode();
//	        if (responseCode == HttpURLConnection.HTTP_OK) {
//	            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//	            String inputLine;
//	            StringBuilder response = new StringBuilder();
//
//	            while ((inputLine = reader.readLine()) != null) {
//	                response.append(inputLine);
//	            }
//	            reader.close();
//
//	            JSONObject jsonResponse = new JSONObject(response.toString());
//	            String lyrics = jsonResponse.getString("lyrics");
//	            System.out.println();
//	            System.out.println(lyrics);
//	        } else {
//	            System.out.println("Error: Unable to fetch lyrics. Response code: " + responseCode);
//	        }
//		} catch (Exception e) {
//			System.out.println("Something went wrong while fetching song lyrics...");
//		}
//	}
	
//	private String getSongData(String title, Song song) {
//	    StringBuilder songDetails = new StringBuilder();
//	    if (song != null) {
//	        if (title.equals("Most Streamed Artist")) {
//	            songDetails.append(String.format("%-25s - %-25s %s", title, song.getArtistName(), timeFormater(song.getDuration())));
//	        } else {
//	            String songTitle = song.getSongTitle();
//	            String artistName = song.getArtistName();
//	            String duration = timeFormater(song.getDuration());
//	            
//	            // Check if the song count is zero, if not, include it in the output
//	            if (!duration.isEmpty()) {
//	                songDetails.append(String.format("%-25s - %-25s %-25s %s", title, songTitle, artistName, duration));
//	            } else {
//	                songDetails.append(String.format("%-25s - %-25s %-25s", title, songTitle, artistName));
//	            }
//	        }
//	    } else {
//	        songDetails.append(title).append(" - No Song Found");
//	    }
//	    return songDetails.toString();
//	}

//	private String timeFormater(String time) {
//		if(time == null || time.isEmpty()) {
//			return "";
//		}
//			
//		StringBuilder timeInWords = new StringBuilder();
//		String[] times = time.split(":");
//		timeInWords.append(Integer.parseInt(times[0])).append("Hr ")
//					.append(Integer.parseInt(times[1])).append("Min");
//		return timeInWords.toString();
//	}
}