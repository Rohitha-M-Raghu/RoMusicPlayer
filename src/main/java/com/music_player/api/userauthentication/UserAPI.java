//$Id$
package com.music_player.api.userauthentication;

import com.music_player.api.userauthentication.util.User;

public interface UserAPI {

	String login(User userPOJO) throws Exception;
	String signup(User userPOJO) throws Exception;
	String logout(String userName) throws Exception;
}
