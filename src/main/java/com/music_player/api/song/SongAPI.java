//$Id$
package com.music_player.api.song;

import java.sql.SQLException;
import java.util.List;

import com.music_player.api.song.util.Song;

public interface SongAPI {

	Song getSongDetails(int songId, boolean isIncludeSongUrl) throws Exception;

	List<Song> getSongs(boolean isIncludeSongUrl) throws SQLException;

}
