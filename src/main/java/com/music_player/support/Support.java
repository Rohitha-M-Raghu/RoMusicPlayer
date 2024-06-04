//$Id$
package com.music_player.support;

import com.music_player.api.userauthentication.AuthorizedUserAPIImpl;
import com.music_player.api.userauthentication.UserAPI;

public class Support {
	
	private Support() {
		
	}
	
	public static UserAPI getAuthorizedUserAPIImpl() {
		return new AuthorizedUserAPIImpl();
	}
}
