package com.example.Insideout.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ChatImageUploadService {

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;

    private final RestTemplate restTemplate;

    public ChatImageUploadService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String uploadImage(MultipartFile file) throws IOException {
        String mimeType = file.getContentType();
        if (mimeType == null || !mimeType.startsWith("image/")) {
            throw new RuntimeException("허용되지 않는 파일 형식입니다: " + mimeType);
        }

        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new RuntimeException("파일 이름을 확인할 수 없습니다.");
        }

        String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
        String uniqueFileName = UUID.randomUUID().toString() + "_" + safeFileName;

        String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + uniqueFileName;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + supabaseKey);
        headers.setContentType(MediaType.parseMediaType(mimeType));

        HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
        ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("파일 업로드 실패: " + response.getBody());
        }

        return supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + uniqueFileName;
    }
} 