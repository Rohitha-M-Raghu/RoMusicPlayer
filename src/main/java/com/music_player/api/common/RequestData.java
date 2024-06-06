package com.music_player.api.common;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RequestData {
    private Map<String, String> pathParams = new HashMap<>();
    private Map<String, String> queryParams = new HashMap<>();
    private JSONObject requestBodyJSON = new JSONObject();

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public void setPathParams(Map<String, String> pathParams) {
        this.pathParams = pathParams;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public void setQueryParams(Map<String, String> queryParams) {
        this.queryParams = queryParams;
    }

    public JSONObject getRequestBodyJSON() {
        return requestBodyJSON;
    }

    public void setRequestBodyJSON(JSONObject bodyParams) {
        this.requestBodyJSON = bodyParams;
    }
}
