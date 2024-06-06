//$Id$
package com.music_player.api.service.user;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.websocket.AuthenticationException;
import org.json.JSONObject;

import com.music_player.api.common.ApiConfig;
import com.music_player.api.common.RequestData;
import com.music_player.api.common.Response;
import com.music_player.api.common.utils.JacksonUtils;
import com.music_player.api.userauthentication.util.User;
import com.music_player.support.Support;

public class UserAuthenticationService {
	
	private static final Logger LOGGER = Logger.getLogger(UserAuthenticationService.class.getName());
	
	public Response login(RequestData requestData) {
		JSONObject requestBodyJson = requestData.getRequestBodyJSON();
		
		String userName = requestBodyJson.getString("username");
		String password = requestBodyJson.getString("password");
		User user;
		if(checkIfEmail(userName)) {
			String emailId = userName;
			user = new User.Builder(emailId)
					.password(password)
					.build();
		} else {	
			user = new User.Builder(userName, password)
					.build();
		}
		
		try {
			String token = Support.getAuthorizedUserAPIImpl().login(user);
			Cookie cookie = new Cookie("token", token);
			return new Response.Builder().noContent().cookie(cookie).build();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response signup(RequestData requestData) {
		JSONObject requestBodyJson = requestData.getRequestBodyJSON();

		String firstName = requestBodyJson.getString("firstName");
		String lastName = requestBodyJson.getString("lastName");
		String emailId = requestBodyJson.getString("emailId");
		String password = requestBodyJson.getString("password");
		User user = new User.Builder(emailId)
				.firstName(firstName)
				.lastName(lastName)
				.password(password)
				.build();
		try {
			String token = Support.getAuthorizedUserAPIImpl().signup(user);
			if(token == null) {
				throw new Exception("Internal Server Error");
			}
			Cookie cookie = new Cookie("token", token);
			return new Response.Builder().noContent().cookie(cookie).build();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response logout(RequestData requestData) {
		JSONObject requestBodyJson = requestData.getRequestBodyJSON();

		// implement
		return new Response.Builder().noContent().build();

	}
	
	private Response buildErrorResponse(Exception exception) {
		JSONObject errorMsg = new JSONObject();
		errorMsg.put("ErrorMsg", exception.getMessage());
        try {
            if (exception.getClass().equals(AuthenticationException.class)) {
                return new Response.Builder()
                        .statusCode(HttpServletResponse.SC_FORBIDDEN)
                        .responseBody(JacksonUtils.convertJSONObjectToString(errorMsg))
                        .build();
            } else if(exception.getClass().equals(IllegalArgumentException.class)) {
            	return new Response.Builder()
            			.statusCode(HttpServletResponse.SC_BAD_REQUEST)
            			.build();
            } else {
                return new Response.Builder()
                        .statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                        .responseBody(JacksonUtils.convertJSONObjectToString(errorMsg))
                        .build();
            }
        } catch (Exception e) {
            return new Response.Builder()
                    .statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
                    .responseBody(JacksonUtils.convertJSONObjectToString(errorMsg))
                    .build();
        }
    }
	
	private boolean checkIfEmail(String userName) {
		boolean isEmail = false;
		String regex = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\\\.[a-zA-Z]{2,}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(userName);
		return matcher.matches();
	}
}
