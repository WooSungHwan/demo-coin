package com.example.democoin.utils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class JsonUtil {

    private JsonUtil() {}

    private static ObjectMapper objectMapper = customObjectMapper();

    public static <T> String toJson(T data) {
        if(data == null) return null;
        try {
            return objectMapper().valueToTree(data).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(String json, Class<T> classType) {
        if (json == null) return null;
        try {
            return objectMapper().treeToValue(objectMapper().readTree(json), classType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T fromJson(Map<String, Object> json, Class<T> classType) {
        if (json == null) return null;
        try {
            return objectMapper().convertValue(json, classType);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> listFromJson(String json, Class<T> classType) {
        if (json == null) return null;
        List<T> results = new ArrayList<>();
        try {
            JsonNode jsonNode = fromJson(json, JsonNode.class);
            if(!jsonNode.isArray()) {
                log.info("[UPBIT-EXCEPTION] 배열로 안넘어옴");
            }

            ArrayNode arrayNode = (ArrayNode) jsonNode;
            for (JsonNode node : arrayNode) {
                results.add(fromJson(node.toString(), classType));
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ObjectMapper objectMapper() {
        return objectMapper;
    }

    private static ObjectMapper customObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

}
