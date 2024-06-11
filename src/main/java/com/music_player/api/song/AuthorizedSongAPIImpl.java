//$Id$
package com.music_player.api.song;

import java.sql.SQLException;
import java.util.List;

import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;

public class AuthorizedSongAPIImpl implements SongAPI{
	
	@Override
	public Song getSongDetails(int songId, boolean isIncludeSongUrl) throws Exception {
		// check if user exists - decrpt the token from cookie
		// check if songId exists
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		}
		
		return new SongAPIImpl().getSongDetails(songId, isIncludeSongUrl);
	}
	
	@Override
	public List<Song> getSongs(boolean isIncludeSongUrl) throws SQLException {
		return new SongAPIImpl().getSongs(isIncludeSongUrl);

	}
}
