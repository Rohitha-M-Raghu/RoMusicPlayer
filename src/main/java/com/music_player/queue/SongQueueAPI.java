//$Id$
package com.music_player.queue;

import java.sql.SQLException;

public interface SongQueueAPI {
	
	void clearQueue(int userId);

	boolean removeSongFromQueue(int userId);

	void shuffleSongs(int userId) throws SQLException;

	void unShuffleSongs(int userId) throws SQLException;

//	void displayStatistics();

//	void displaySongLyrics(String songTitle, String artistName);

//	void displaySongLyrics();

}