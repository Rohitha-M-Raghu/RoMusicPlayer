//$Id$
package com.music_player.api.common;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class Response {
	private int statusCode = HttpServletResponse.SC_OK;
	private String responseBody;
	private Cookie cookie;
	private String contentType;
    
    private static final Logger logger = Logger.getLogger(Response.class.getName());
	
	public int getStatusCode() {
		return statusCode;
	}

	public String getResponseBody() {
		return responseBody;
	}

	public Cookie getCookie() {
		return cookie;
	}
	
	public void setHttpServletResponse(HttpServletResponse response) {
		response.setStatus(statusCode);
		if(contentType == null) {
			response.setContentType("application/json");
		} else {
			response.setContentType(contentType);
		}
		
		
		
		// recheck this
		PrintWriter out = null;
		if(responseBody != null && !responseBody.isEmpty()) {
			try {
				out = response.getWriter();
				out.write(responseBody);
				out.flush();
			} catch (IOException e) {
	            logger.log(Level.SEVERE, "Error writing response body", e);	
	        } finally {
				if(out != null) {
					out.close();
				}
			}
		}

		
		if(cookie != null) {
			response.addCookie(cookie);
		}
	}
	
	private Response(Builder builder) {
		this.statusCode = builder.statusCode;
		this.responseBody = builder.responseBody;
		this.cookie = builder.cookie;
		this.contentType = builder.contentType;
	}
	
	public static class Builder {
		private int statusCode = HttpServletResponse.SC_OK;
		private String responseBody;
		private Cookie cookie;
		private String contentType;
		
		public Builder statusCode(int statusCode) {
			this.statusCode = statusCode;
			return this;
		}
		
		public Builder responseBody(String responseBody) {
			this.responseBody = responseBody;
			return this;
		}
		
		public Builder cookie(Cookie cookie) {
			this.cookie = cookie;
			return this;
		}
		
		public Builder contentType(String contentType) {
			this.contentType = contentType;
			return this;
		}
		
		// 200
		public Builder ok(String responseBody) {
			if(responseBody == null || responseBody.isEmpty()) {
				this.statusCode = HttpServletResponse.SC_NO_CONTENT;
			} else {
				this.statusCode = HttpServletResponse.SC_OK;
				this.responseBody = responseBody;
			}
			return this;
		}
		
		public Builder noContent() {
			this.statusCode = HttpServletResponse.SC_NO_CONTENT;
			return this;
		}
		
		public Builder created(String responseBody) {
			this.statusCode = HttpServletResponse.SC_CREATED;
			this.responseBody = responseBody;
			return this;
		}
		
		public Response build() {
			return new Response(this);
		}
	}
}
