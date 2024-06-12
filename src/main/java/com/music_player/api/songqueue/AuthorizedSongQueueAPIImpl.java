//$Id$
package com.music_player.api.songqueue;

import java.io.IOException;
import java.sql.SQLException;

import com.music_player.api.song.util.Song;

public class AuthorizedSongQueueAPIImpl implements SongQueueAPI{

	@Override
	public boolean clearQueue(int userId) {
		// validate user
		return new SongQueueAPIImpl().clearQueue(userId);
	}

	@Override
	public void shuffleSongs(int userId) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void unShuffleSongs(int userId) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Song moveToNextTrack(int userId) throws NumberFormatException, SQLException, IOException {
		// validate userId
		return new SongQueueAPIImpl().moveToNextTrack(userId);
	}

	@Override
	public Song moveToPrevTrack(int userId) throws SQLException, IOException {
		// validate userId
		return new SongQueueAPIImpl().moveToPrevTrack(userId);
	}

	
}
