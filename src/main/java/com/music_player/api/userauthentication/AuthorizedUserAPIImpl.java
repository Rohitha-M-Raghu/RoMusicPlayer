//$Id$
package com.music_player.api.userauthentication;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.tomcat.websocket.AuthenticationException;

import com.music_player.api.common.utils.UsernameGenerator;
import com.music_player.api.userauthentication.util.User;
import com.music_player.api.userauthentication.util.UserUtil;

public class AuthorizedUserAPIImpl implements UserAPI{
	
	UserAPIImpl userAPIImpl = new UserAPIImpl();
	
	private static final Logger LOGGER = Logger.getLogger(AuthorizedUserAPIImpl.class.getName());

	@Override
	public String login(User userPOJO) throws Exception {
		
		try {
			String userName = userPOJO.getUserName();
			String emailId = userPOJO.getEmailId();
			String password = userPOJO.getPassword();

			if((userName == null || userName.isEmpty()) && (emailId == null || emailId.isEmpty())) {
				throw new IllegalArgumentException("Both username and email cannot be null");
			}
			
			if(password == null || password.isEmpty()) {
				throw new IllegalArgumentException("Password cannot be empty");
			}
			
			if(userName != null) {
				if(UserUtil.getInstance().isUserExist(userName)) {
					throw new AuthenticationException("Invalid username or password");
				}
				if(!UserUtil.getInstance().validateUserNamePassword(userName, password)) {
					throw new AuthenticationException("Invalid username or password");
				}
			}	
			
			if(emailId != null) {
				if(!UserUtil.getInstance().isEmailExist(emailId)) {
					throw new AuthenticationException("Invalid username or password");
				}
				if(!UserUtil.getInstance().validateEmailPassword(emailId, password)) {
					throw new AuthenticationException("Invalid username or password");
				}
			}
			
			return new UserAPIImpl().login(userPOJO);
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			throw e;
		}
	}

	@Override
	public String signup(User userPOJO) throws Exception {
		String firstName = userPOJO.getFirstName();
		String lastName = userPOJO.getLastname();
		String emailId = userPOJO.getEmailId();
		String password = userPOJO.getPassword();
		
		if((firstName == null || firstName.isEmpty()) && (emailId == null || emailId.isEmpty())) {
			throw new IllegalArgumentException("Both firstname and email is mandatory");
		}
		
		if(password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Password cannot be empty");
		}
		
		// check if emailId already exists
		if(UserUtil.getInstance().isEmailExist(emailId)) {
			throw new AuthenticationException("An account with this email already exists");
		}
		
		Set<String> userNames = UserUtil.getInstance().getAllExistingUserNames();
		String userName = UsernameGenerator.getInstance().generateUniqueUsername(firstName, userNames);
		
		User newUser = new User.Builder(userName, password)
						.firstName(firstName)
						.lastName(lastName)
						.emailId(emailId)
						.build();
		return new UserAPIImpl().signup(newUser); 
	}

	@Override
	public String logout(String userName) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
