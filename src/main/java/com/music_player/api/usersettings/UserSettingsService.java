//$Id$
package com.music_player.api.usersettings;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.websocket.AuthenticationException;
import org.json.JSONObject;

import com.music_player.api.common.RequestData;
import com.music_player.api.common.Response;
import com.music_player.api.common.utils.JacksonUtils;
import com.music_player.api.userpreference.util.SettingsMode;
import com.music_player.api.userpreference.util.UserSettings;
import com.music_player.support.Support;

public class UserSettingsService {
	
	private static final Logger LOGGER = Logger.getLogger(UserSettingsService.class.getName());
	
	public Response shuffle(RequestData requestData) {
		boolean shuffleSettings = (boolean) requestData.getRequestBodyJSON().get("shuffle");
		
		// decrypt token and get userID
		int userId = 3;
		
		try {
			Support.getAuthorizedUserPreferenceAPIImpl().changeSettings(userId, SettingsMode.SHUFFLE, shuffleSettings);
			return new Response.Builder().noContent().build();
			
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
		
	}
	
	public Response loop(RequestData requestData) {
		boolean loopSettings = (boolean) requestData.getRequestBodyJSON().get("loop");
		
		// decrypt token and get userID
		int userId = 3;
		
		try {
			Support.getAuthorizedUserPreferenceAPIImpl().changeSettings(userId, SettingsMode.LOOP, loopSettings);
			return new Response.Builder().noContent().build();
			
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
		
	}
	
	public Response autoplay(RequestData requestData) {
		boolean autoplaySettings = (boolean) requestData.getRequestBodyJSON().get("autoplay");
		
		// decrypt token and get userID
		int userId = 3;
		
		try {
			Support.getAuthorizedUserPreferenceAPIImpl().changeSettings(userId, SettingsMode.AUTOPLAY, autoplaySettings);
			return new Response.Builder().noContent().build();
			
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
		
	}
	
	public Response getSettings(RequestData requestData) {
		boolean autoplaySettings = (boolean) requestData.getRequestBodyJSON().get("autoplay");
		
		// decrypt token and get userID
		int userId = 3;
		
		try {
			UserSettings userSettings = Support.getAuthorizedUserPreferenceAPIImpl().getSettings(userId);
			return new Response.Builder().ok(JacksonUtils.serialize(userSettings)).build();
			
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
}
