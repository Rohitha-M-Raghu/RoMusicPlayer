//$Id$
package com.music_player.api.userpreference.util;

public class UserSettings {
	private boolean isShuffle;
	private boolean isLoop;
	private boolean isAutoplay;
	
	public boolean isShuffle() {
		return isShuffle;
	}

	public boolean isLoop() {
		return isLoop;
	}

	public boolean isAutoplay() {
		return isAutoplay;
	}

	private UserSettings(Builder builder) {
		this.isShuffle = builder.isShuffle;
		this.isLoop = builder.isLoop;
		this.isAutoplay = builder.isAutoplay;
	}
	
	public static class Builder {
		private boolean isShuffle;
		private boolean isLoop;
		private boolean isAutoplay;
		
		public Builder() {
			
		}
		
		public Builder isShuffle(boolean isShuffle) {
			this.isShuffle = isShuffle;
			return this;
		}
		
		public Builder isLoop(boolean isLoop) {
			this.isLoop = isLoop;
			return this;
		}
		
		public Builder isAutoplay(boolean isAutoplay) {
			this.isAutoplay = isAutoplay;
			return this;
		}
		
		public UserSettings build() {
			return new UserSettings(this);
		}
	}
}
