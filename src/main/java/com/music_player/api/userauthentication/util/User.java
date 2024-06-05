//$Id$
package com.music_player.api.userauthentication.util;

public class User {
	private String userName;
	private String emailId;
	private String password;
	private String firstName;
	private String lastname;
	
	public String getFirstName() {
		return firstName;
	}

	public String getLastname() {
		return lastname;
	}

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
		private String firstName;
		private String lastName;
		
		public Builder(String userName, String password) {
			this.userName = userName;
			this.password = password;
		}
				
		public Builder(String emailId) {
			this.emailId = emailId;
		}
		
		public Builder password(String password) {
			this.password = password;
			return this;
		}
		
		public Builder emailId(String emailId) {
			this.emailId = emailId;
			return this;
		}
		
		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}
		
		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}
				
		public User build() {
			return new User(this);
		}
	}
}
