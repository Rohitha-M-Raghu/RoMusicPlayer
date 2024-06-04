//$Id$
package com.music_player.api.service.user;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.websocket.AuthenticationException;
import org.json.JSONObject;

import com.music_player.api.common.Response;
import com.music_player.api.common.utils.JacksonUtils;
import com.music_player.api.userauthentication.util.User;
import com.music_player.support.Support;

public class UserAuthenticationService {
	
	private static final Logger LOGGER = Logger.getLogger(UserAuthenticationService.class.getName());
	
	public Response login(JSONObject requestBodyJson) {
		String userName = requestBodyJson.getString("username");
		String password = requestBodyJson.getString("password");
		
		User user = new User.Builder(userName, password).build();
		
		try {
			String token = Support.getAuthorizedUserAPIImpl().login(user);
			Cookie cookie = new Cookie("token", token);
			return new Response.Builder().noContent().cookie(cookie).build();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
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
}
