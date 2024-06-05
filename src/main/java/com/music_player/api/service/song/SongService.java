//$Id$
package com.music_player.api.service.song;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.websocket.AuthenticationException;
import org.json.JSONObject;

import com.music_player.api.common.Response;
import com.music_player.api.common.utils.JacksonUtils;
import com.music_player.api.song.util.Song;
import com.music_player.support.Support;

public class SongService {

	private static final Logger LOGGER = Logger.getLogger(SongService.class.getName());
	
	public Response getSongDetails(JSONObject requestBodyJson)   {
		int songId = requestBodyJson.getInt("songId");
		try {
			Song song = Support.getAuthorizedSongAPIImpl().getSongDetails(songId);
			return new Response.Builder().ok(JacksonUtils.serialize(song)).build();
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
            } else if(exception.getClass().equals(NullPointerException.class)) {
            	return new Response.Builder()
            			.statusCode(HttpServletResponse.SC_NOT_FOUND)
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
