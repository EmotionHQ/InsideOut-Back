package com.example.Insideout.service;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    @Value("${jwt.secret}") // application.properties에서 Secret Key 가져오기
    private String rawSecretKey;

    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // 10시간
    private Key secretKey;

    /**
     * Secret Key 초기화
     */
    @PostConstruct
    public void init() {
        // Base64 인코딩된 Secret Key 생성 및 Key 객체 변환
        byte[] decodedKey = Base64.getEncoder().encode(rawSecretKey.getBytes());
        this.secretKey = new SecretKeySpec(decodedKey, SignatureAlgorithm.HS256.getJcaName());
    }

    /**
     * 사용자 이름을 기반으로 JWT 생성
     */
    public String generateToken(String userId) {
        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT에서 사용자 이름 추출
     */
    public String extractUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    /**
     * JWT 토큰의 유효성을 검증
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * JWT 유효성 검증 및 userId 추출
     */
    public String validateAndExtractUserId(String authorizationToken) {
        if (authorizationToken == null || !authorizationToken.startsWith("Bearer ")) {
            throw new JwtException("Invalid Authorization header format");
        }

        String token = authorizationToken.substring(7);

        if (!validateToken(token)) {
            throw new JwtException("Invalid or expired token");
        }

        return extractUserId(token);
    }
}