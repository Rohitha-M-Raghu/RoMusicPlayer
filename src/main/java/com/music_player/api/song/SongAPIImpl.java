//$Id$
package com.music_player.api.song;

import java.sql.SQLException;

import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;

public class SongAPIImpl implements SongAPI{

	@Override
	public Song getSongDetails(int songId) throws SQLException {
		return SongUtil.getInstance().getSongDetails(songId);
	}

}
