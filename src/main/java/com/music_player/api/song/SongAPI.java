//$Id$
package com.music_player.api.song;

import com.music_player.api.song.util.Song;

public interface SongAPI {

	Song getSongDetails(int songId) throws Exception;

}
