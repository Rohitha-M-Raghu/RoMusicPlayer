//$Id$
package com.music_player.api.userauthentication;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tomcat.websocket.AuthenticationException;

import com.music_player.api.userauthentication.util.User;
import com.music_player.api.userauthentication.util.UserUtil;

public class AuthorizedUserAPIImpl implements UserAPI{
	
	UserAPIImpl userAPIImpl = new UserAPIImpl();
	
	private static final Logger LOGGER = Logger.getLogger(AuthorizedUserAPIImpl.class.getName());

	@Override
	public String login(User userPOJO) throws Exception {
		
		try {
			// check if the user exists
			String userName = userPOJO.getUserName();
			if(!UserUtil.getInstance().isUserExist(userName)) {
				throw new AuthenticationException("Invalid username or password");
				// 401 Unauthorized
			}
			String password = userPOJO.getPassword();
			if(!UserUtil.getInstance().validateUserNamePassword(userName, password)) {
				// 401 Unauthorized
				throw new AuthenticationException("Invalid username or password");
			}
			return new UserAPIImpl().login(userPOJO);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			throw e;
		}
	}
}
