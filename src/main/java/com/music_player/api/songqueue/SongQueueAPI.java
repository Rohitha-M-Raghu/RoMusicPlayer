//$Id$
package com.music_player.api.songqueue;

import java.io.IOException;
import java.sql.SQLException;

import com.music_player.api.song.util.Song;

public interface SongQueueAPI {

	boolean clearQueue(int userId);

	void shuffleSongs(int userId) throws SQLException;

	void unShuffleSongs(int userId) throws SQLException;

	Song moveToNextTrack(int userId) throws NumberFormatException, SQLException, IOException;

	Song moveToPrevTrack(int userId) throws SQLException, IOException;

}
