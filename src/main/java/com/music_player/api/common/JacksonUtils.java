//$Id$
package com.music_player.api.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;

import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;

public class JacksonUtils {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing object to JSON", e);
        }
    }

    public static <T> T deserialize(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON to object", e);
        }
    }

    public static void serializeToFile(Object obj, File file) {
        try {
            objectMapper.writeValue(file, obj);
        } catch (IOException e) {
            throw new RuntimeException("Error serializing object to file", e);
        }
    }

    // Convert Reader to String
    public static String convertReaderToString(Reader reader) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(reader);
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    // Convert String to JSONObject
    public static JSONObject convertStringToJSONObject(String jsonString) {
        return new JSONObject(jsonString);
    }

    // Convert JSONObject to String
    public static String convertJSONObjectToString(JSONObject jsonObject) {
        return jsonObject.toString();
    }
}
