//$Id$
package com.music_player.api.song;

import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;

public class AuthorizedSongAPIImpl implements SongAPI{
	
	@Override
	public Song getSongDetails(int songId) throws Exception {
		// check if user exists - decrpt the token from cookie
		// check if songId exists
		if(!SongUtil.getInstance().checkIfSongExists(songId)) {
			throw new NullPointerException("Song not found");
		}
		
		return new SongAPIImpl().getSongDetails(songId);
	}
}
