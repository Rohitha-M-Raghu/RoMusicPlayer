//$Id$
package com.music_player.api.userauthentication;

import com.music_player.api.common.utils.JwtUtil;
import com.music_player.api.userauthentication.util.User;

public class UserAPIImpl implements UserAPI{
	
	@Override
	public String login(User userPOJO) {
		String userName = userPOJO.getUserName();
		// need to generate token and return
//		return JwtUtil.getInstance().generateToken(userName);
		return "token";
	}
}
