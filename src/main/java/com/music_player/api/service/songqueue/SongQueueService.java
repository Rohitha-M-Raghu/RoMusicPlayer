//$Id$
package com.music_player.api.service.songqueue;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.websocket.AuthenticationException;
import org.json.JSONObject;

import com.music_player.api.common.RequestData;
import com.music_player.api.common.Response;
import com.music_player.api.common.utils.JacksonUtils;
import com.music_player.api.song.util.Song;
import com.music_player.support.Support;

public class SongQueueService {
	// clear queue
	// skip to next track
	// skip to prev track
	private static final Logger LOGGER = Logger.getLogger(SongQueueService.class.getName());
	
	public Response clearQueue(RequestData requestData) {
		// get userId from token
		int userId = 3;
		try {
			if(Support.getAuthorizedSongQueueAPIImpl().clearQueue(userId)) {
				return new Response.Builder().noContent().build();
			} else {
				return new Response.Builder()
						.statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
						.build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response moveToNextTrack(RequestData requestData)   {
		// get userId from token
		int userId = 3;
		try {
			Song song = Support.getAuthorizedSongQueueAPIImpl().moveToNextTrack(userId);
			if(song == null) {
				return new Response.Builder().noContent().build();
			}
			return new Response.Builder().ok(JacksonUtils.serialize(song)).build();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response moveToPrevTrack(RequestData requestData)   {
		// get userId from token
		int userId = 3;
		try {
			Song song = Support.getAuthorizedSongQueueAPIImpl().moveToPrevTrack(userId);
			if(song == null) {
				return new Response.Builder().noContent().build();
			}
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
