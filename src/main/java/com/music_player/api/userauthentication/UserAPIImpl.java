//$Id$
package com.music_player.api.userauthentication;

import com.music_player.api.common.utils.JwtUtil;
import com.music_player.api.userauthentication.util.User;
import com.music_player.api.userauthentication.util.UserUtil;

public class UserAPIImpl implements UserAPI{
	
	@Override
	public String login(User userPOJO) {
		String userName = userPOJO.getUserName();
		// need to generate token and return
//		return JwtUtil.getInstance().generateToken(userName);
		return "token";
	}

	@Override
	public String signup(User userPOJO) throws Exception {
		
		boolean isUserAdded = UserUtil.getInstance().addNewUser(userPOJO);
		if(isUserAdded) {
			// need to generate token and return
//			return JwtUtil.getInstance().generateToken(userName);
			return "token";
		}
		return null;
	}

	@Override
	public String logout(String userName) throws Exception {
		// to do
		return null;
	}
}
