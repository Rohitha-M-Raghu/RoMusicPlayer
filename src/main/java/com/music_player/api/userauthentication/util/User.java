//$Id$
package com.music_player.api.userauthentication.util;

public class User {
	private String userName;
	private String emailId;
	private String password;
	
	public String getUserName() {
		return userName;
	}

	public String getEmailId() {
		return emailId;
	}

	public String getPassword() {
		return password;
	}

	private User(Builder builder) {
		this.userName = builder.userName;
		this.emailId = builder.emailId;
		this.password = builder.password;
	}
	
	public static class Builder {
		private String userName;
		private String emailId;
		private String password;
		
		public Builder(String userName, String password) {
			this.userName = userName;
			this.password = password;
		}
		
		public Builder emailId(String emailId) {
			this.emailId = emailId;
			return this;
		}
		
		public User build() {
			return new User(this);
		}
	}
}
