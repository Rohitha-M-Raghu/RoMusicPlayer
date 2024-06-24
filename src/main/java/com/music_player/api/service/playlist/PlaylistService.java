//$Id$
package com.music_player.api.service.playlist;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.websocket.AuthenticationException;
import org.json.JSONObject;

import com.music_player.api.common.DuplicateException;
import com.music_player.api.common.RequestData;
import com.music_player.api.common.Response;
import com.music_player.api.common.utils.JacksonUtils;
import com.music_player.api.song.util.Song;
import com.music_player.support.Support;

public class PlaylistService {
	
	private static final Logger LOGGER = Logger.getLogger(PlaylistService.class.getName());
	
	public Response createPlaylist(RequestData requestData) {
		// get userId from token
		int userId = 3; 
		String playlistName = requestData.getRequestBodyJSON().getString("playlistName");
		try {
			if(Support.getAuthorizedPlaylistAPIImpl().createPlayList(userId, playlistName)) {
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
	
	public Response addSongToPlayList(RequestData requestData) {
		// get userId from token
		int userId = 3; 
		int songId = (int) requestData.getRequestBodyJSON().get("songId");
		int playlistId = Integer.parseInt(requestData.getPathParams().get("param1"));

		try {
			if(Support.getAuthorizedPlaylistAPIImpl().addSongToPlayList(userId, playlistId, songId)) {
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
	
	public Response addCurrentPlayingSongToPlayList(RequestData requestData) {
		// get userId from token
		int userId = 3; 
		int playlistId = Integer.parseInt(requestData.getPathParams().get("param1"));

		try {
			if(Support.getAuthorizedPlaylistAPIImpl().addCurrentPlayingSongToPlayList(userId, playlistId)) {
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
	
	public Response removeSongFromPlayList(RequestData requestData) {
		// get userId from token
		int userId = 3; 
		int playlistId = Integer.parseInt(requestData.getPathParams().get("param1"));
		int songId = Integer.parseInt(requestData.getPathParams().get("param2"));

		try {
			if(Support.getAuthorizedPlaylistAPIImpl().removeSongFromPlaylist(userId, songId, playlistId)) {
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
	
	public Response getPlaylistSongs(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int playlistId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			List<Song> songs = Support.getAuthorizedPlaylistAPIImpl().getPlaylistSongs(userId, playlistId);
			
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
	
	public Response renamePlaylist(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int playlistId = Integer.parseInt(requestData.getPathParams().get("param1"));
		String playlistName = requestData.getRequestBodyJSON().getString("playlistName");
		try {
			if(Support.getAuthorizedPlaylistAPIImpl().renamePlaylist(userId, playlistId, playlistName)) {
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
	
	public Response deletePlaylist(RequestData requestData) {
		// get userId from token
		int userId = 3;
		int playlistId = Integer.parseInt(requestData.getPathParams().get("param1"));
		try {
			if(Support.getAuthorizedPlaylistAPIImpl().deletePlaylist(userId, playlistId)) {
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
	
	public Response playPlaylist(RequestData requestData)   {
		// get userID from token
		int userId = 3;
		int playlistId = Integer.parseInt(requestData.getPathParams().get("param1"));
		
		try {
			Song song = Support.getAuthorizedPlaylistAPIImpl().playPlaylist(userId, playlistId);
			return new Response.Builder().ok(JacksonUtils.serialize(song)).build();
		} catch (Exception e) {
			LOGGER.log(Level.WARNING, e.getMessage());
			return buildErrorResponse(e);
		}
	}
	
	public Response getPlaylistListing(RequestData requestData) {
		// get userID from token
		int userId = 3;
		try {
			JSONObject playlistListingJSON = Support.getAuthorizedPlaylistAPIImpl().getPlaylistListing(userId);
			return new Response.Builder().ok(playlistListingJSON.toString()).build();
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
            } else if (exception.getClass().equals(DuplicateException.class)) {
            	return new Response.Builder()
            			.statusCode(HttpServletResponse.SC_CONFLICT)
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
