package com.statewide.login.utils;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

/*Json util for map to json string and json to map */
public class JsonUtil {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String mapToJson(Map<String, Object> map) throws IOException {
        return objectMapper.writeValueAsString(map);
    }

    public static Map<String, Object> jsonToMap(String json) throws IOException {
        return objectMapper.readValue(json, Map.class);
    }
}
