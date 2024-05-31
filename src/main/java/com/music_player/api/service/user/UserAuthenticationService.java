//$Id$
package com.music_player.api.service.user;

import org.json.JSONObject;

public class UserAuthenticationService {
	
	public void login(JSONObject requestBodyJson, JSONObject responseJson) {
		responseJson.put("success", true);
	}
}
