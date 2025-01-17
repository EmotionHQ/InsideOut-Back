package com.example.Insideout.service;

import com.example.Insideout.dto.MessageResponse;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class FastApiClient {

    private final RestTemplate restTemplate;

    public FastApiClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fast API에 sessionId 전달, MessageResponse 형태로 반환
     */
    public MessageResponse sendMessageToFastApi(Long sessionId) {
        try {
            String url = "http://localhost:8000/api/process";
            Map<String, Long> request = Map.of("sessionId", sessionId);
            return restTemplate.postForObject(url, request, MessageResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 연결 실패", e);
        }
    }
}