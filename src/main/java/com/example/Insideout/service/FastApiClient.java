package com.example.Insideout.service;

import com.example.Insideout.dto.MessageResponse;
import com.example.Insideout.dto.SessionSummaryResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
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
            // String url = "http://localhost:8000/api/process";
            String url = "https://insideout-ai-production.up.railway.app/api/process";
            Map<String, Long> request = Map.of("sessionId", sessionId);

            // FastAPI로부터 응답 받기
            MessageResponse response = restTemplate.postForObject(url, request, MessageResponse.class);

            // 현재 시간으로 생성 시간 설정
            if (response != null && response.getCreatedAt() == null) {
                response.setCreatedAt(LocalDateTime.now());
            }

            return response;
        } catch (Exception e) {
            throw new RuntimeException("FastAPI 연결 실패", e);
        }
    }

    /**
     * Fast API에 sessionId 전달 > 개선사항, 상태, 요약 반환
     */
    public SessionSummaryResponse getSessionSummary(Long sessionId) {
        String url = "http://localhost:8000/api/session/summary";

        Map<String, Long> requestBody = new HashMap<>();
        requestBody.put("sessionId", sessionId);

        // FastAPI로 요청 및 응답 처리
        return restTemplate.postForObject(url, requestBody, SessionSummaryResponse.class);
    }

    /**
     * 부서원들의 세션 아이디들 전달 > 부서의 개선 사항 반환
     */
    public String getImprovements(List<Long> sessionIds) {
        String url = "http://localhost:8000/api/department/improvements";
        Map<String, List<Long>> request = Map.of("sessionIds", sessionIds);

        return restTemplate.postForObject(url, request, String.class);
    }
}