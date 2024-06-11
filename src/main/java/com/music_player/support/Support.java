//$Id$
package com.music_player.support;

import com.music_player.api.song.SongAPI;
import com.music_player.api.userauthentication.AuthorizedUserAPIImpl;
import com.music_player.api.userauthentication.UserAPI;
import com.music_player.api.userpreference.AuthorizedUserPreferenceAPIImpl;
import com.music_player.api.userpreference.UserPreferenceAPI;
import com.music_player.api.song.AuthorizedSongAPIImpl;

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
}
