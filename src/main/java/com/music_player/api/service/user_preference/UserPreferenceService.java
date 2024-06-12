//$Id$
package com.music_player.api.service.user_preference;

import java.util.List;
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

public class UserPreferenceService {
	
	private static final Logger LOGGER = Logger.getLogger(UserPreferenceService.class.getName());
	
	public Response likeSong(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int songId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			if(Support.getAuthorizedUserPreferenceAPIImpl().likeASong(userId, songId)) {
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
	
	public Response likeCurrentPlayingSong(RequestData requestData) {
		// get userId from token
		int userId = 3;
		try {
			if(Support.getAuthorizedUserPreferenceAPIImpl().likeCurrentPlayingSong(userId)) {
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
	
	public Response unlikeSong(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int songId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			if(Support.getAuthorizedUserPreferenceAPIImpl().unlikeASong(userId, songId)) {
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
	
	public Response getLikedSongs(RequestData requestData) {
		// get userId from token
		int userId = 3;
		try {
			List<Song> songs = Support.getAuthorizedUserPreferenceAPIImpl().getLikedSongs(userId);
			
			if(songs.isEmpty()) {
				return new Response.Builder().noContent().build();
			} else {
				JSONObject responseBodyJSON = new JSONObject();
				responseBodyJSON.put("data", songs);
				return new Response.Builder().ok(responseBodyJSON.toString()).build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response getFrequentlyPlayedSongs(RequestData requestData) {
		// get userId from token
		boolean isCountNeeded = false;
		
		if(requestData.getQueryParams().containsKey("include")) {
			String[] includeOptions = requestData.getQueryParams().get("include").split(",");
			for(String option: includeOptions) {
				if(option.equals("count")) {
					isCountNeeded = true;
				}
			}
		}
		int userId = 3;
		try {
			List<Song> songs = Support.getAuthorizedUserPreferenceAPIImpl().getFrequentlyPlayedSongs(userId, isCountNeeded);
			if(songs.isEmpty()) {
				return new Response.Builder().noContent().build();
			} else {
				JSONObject responseBodyJSON = new JSONObject();
				responseBodyJSON.put("data", songs);
				return new Response.Builder().ok(responseBodyJSON.toString()).build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response likePlaylist(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int playlistId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			if(Support.getAuthorizedUserPreferenceAPIImpl().likeAPlayList(userId, playlistId)) {
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
	
	public Response unlikePlaylist(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int playlistId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			if(Support.getAuthorizedUserPreferenceAPIImpl().unlikePlaylist(userId, playlistId)) {
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
