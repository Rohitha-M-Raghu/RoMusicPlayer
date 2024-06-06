//$Id$
package com.music_player.api.common;

public class UnprocessableEntityException extends Exception{
	public UnprocessableEntityException(String errorMsg) {
		super(errorMsg);
	}
}
