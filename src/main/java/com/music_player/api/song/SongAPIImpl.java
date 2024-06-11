//$Id$
package com.music_player.api.song;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import com.music_player.api.song.util.Song;
import com.music_player.api.song.util.SongUtil;

public class SongAPIImpl implements SongAPI{

	@Override
	public Song getSongDetails(int songId, boolean isIncludeSongUrl) throws SQLException, IOException {
		return SongUtil.getInstance().getSongDetails(songId, isIncludeSongUrl);
	}

	@Override
	public List<Song> getSongs(boolean isIncludeSongUrl) throws SQLException {
		return SongUtil.getInstance().getAllSongs(isIncludeSongUrl);
	}
}
