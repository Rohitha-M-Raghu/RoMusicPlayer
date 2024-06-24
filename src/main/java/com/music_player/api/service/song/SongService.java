//$Id$
package com.music_player.api.service.song;

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

public class SongService {

	private static final Logger LOGGER = Logger.getLogger(SongService.class.getName());
	
	public Response getSongDetails(RequestData requestData)   {
		
		int songId = Integer.parseInt(requestData.getPathParams().get("param1"));
		boolean isIncludeSongUrl = false;
		
		if(requestData.getQueryParams().containsKey("include")) {
			String[] includeOptions = requestData.getQueryParams().get("include").split(",");
			for(String option: includeOptions) {
				if(option.equals("songUrl")) {
					isIncludeSongUrl = true;
				}
			}
		}
		try {
			Song song = Support.getAuthorizedSongAPIImpl().getSongDetails(songId, isIncludeSongUrl);
			return new Response.Builder().ok(JacksonUtils.serialize(song)).build();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response getCurrentPlayingSong(RequestData requestData) {
		int userId = 3;
		try {
			Song song = Support.getAuthorizedSongAPIImpl().getCurrentPlatingSong(userId);
			return new Response.Builder().ok(JacksonUtils.serialize(song)).build();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response getAllSongs(RequestData requestData) {
		boolean isIncludeSongUrl = false;
		
		if(requestData.getQueryParams().containsKey("include")) {
			String[] includeOptions = requestData.getQueryParams().get("include").split(",");
			for(String option: includeOptions) {
				if(option.equals("songUrl")) {
					isIncludeSongUrl = true;
				}
			}
		}
		try {
			List<Song> songs = Support.getAuthorizedSongAPIImpl().getSongs(isIncludeSongUrl);
			
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
	
	public Response getSongLyrics(RequestData requestData) {
		// get userId from token
		int userId = 3; 
		int songId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			String songLyrics = Support.getAuthorizedSongAPIImpl().getSongLyrics(songId);
			JSONObject responseBodyJSON = new JSONObject();
			responseBodyJSON.put("data", songLyrics);
			return new Response.Builder().ok(responseBodyJSON.toString()).build();

		}catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response playSong(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int songId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			if(Support.getAuthorizedSongAPIImpl().playSong(userId, songId)) {
				return new Response.Builder().noContent().build();
			} else {
				return new Response.Builder().statusCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR)
						.build();
			}
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response addSongToQueue(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int songId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			if(Support.getAuthorizedSongAPIImpl().addSongToQueue(userId, songId)) {
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
	
	public Response removeSongFromQueue(RequestData requestData) {
		// get userId from token
		int userId = 3;
		double order = 0;
		int songId = Integer.parseInt(requestData.getPathParams().get("param1"));
		if(requestData.getQueryParams().containsKey("order")) {
			order = Double.parseDouble(requestData.getQueryParams().get("order"));
			
		}
		try {
			if(Support.getAuthorizedSongAPIImpl().removeSongFromQueue(userId, songId, order)) {
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
	
	public Response playSongNext(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int songId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			if(Support.getAuthorizedSongAPIImpl().playSongNext(userId, songId)) {
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
	
	public Response playAllSongs(RequestData requestData) {
		int userId = 3;
		try {
			Song song = Support.getAuthorizedSongAPIImpl().playAllSongs(userId);
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
