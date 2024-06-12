//$Id$
package com.music_player.support;

import com.music_player.api.playlist.AuthorizedPlaylistAPIImpl;
import com.music_player.api.playlist.PlaylistAPI;
import com.music_player.api.song.AuthorizedSongAPIImpl;
import com.music_player.api.song.SongAPI;
import com.music_player.api.songqueue.AuthorizedSongQueueAPIImpl;
import com.music_player.api.songqueue.SongQueueAPI;
import com.music_player.api.userauthentication.AuthorizedUserAPIImpl;
import com.music_player.api.userauthentication.UserAPI;
import com.music_player.api.userpreference.AuthorizedUserPreferenceAPIImpl;
import com.music_player.api.userpreference.UserPreferenceAPI;

public class Support {
	
	private Support() {
		
	}
	
	public static UserAPI getAuthorizedUserAPIImpl() {
		return new AuthorizedUserAPIImpl();
	}
	
	public static SongAPI getAuthorizedSongAPIImpl() {
		return new AuthorizedSongAPIImpl();
	}
	
	public static UserPreferenceAPI getAuthorizedUserPreferenceAPIImpl() {
		return new AuthorizedUserPreferenceAPIImpl();
	}
	
	public static SongQueueAPI getAuthorizedSongQueueAPIImpl() {
		return new AuthorizedSongQueueAPIImpl();
	}
	
	public static PlaylistAPI getAuthorizedPlaylistAPIImpl() {
		return new AuthorizedPlaylistAPIImpl();
	}
}
