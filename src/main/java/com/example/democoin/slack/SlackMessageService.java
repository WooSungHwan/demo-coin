package com.example.democoin.slack;

import com.example.democoin.configuration.properties.SlackProperties;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class SlackMessageService {

    private final SlackProperties properties;

    public boolean message(String message) {
        RestTemplate restTemplate = new RestTemplate();

        Map<String ,Object> body = new HashMap<>();
        String token = properties.getToken();
        body.put("channel", properties.getChannel());
        body.put("text", message);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer "+ token);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<Map<String, Object>>(body, headers);

        ResponseEntity<ObjectNode> exchange = restTemplate.exchange(properties.getMessageUrl(), HttpMethod.POST, entity, ObjectNode.class);

        JsonNode jsonNode = exchange.getBody();
        if (!jsonNode.hasNonNull("ok") || !jsonNode.findValue("ok").booleanValue()) {
            throw new RuntimeException("슬랙메시지를 보내는데 실패하였습니다.\r\n 실패사유 : " + (jsonNode.hasNonNull("error") ? jsonNode.findValue("error").textValue() : ""));
        }
        return true;
    }

}
