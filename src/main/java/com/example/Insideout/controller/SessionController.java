package com.example.Insideout.controller;

import com.example.Insideout.dto.MessageRequest;
import com.example.Insideout.dto.MessageResponse;
import com.example.Insideout.dto.ORSRequest;
import com.example.Insideout.dto.SessionEndRequest;
import com.example.Insideout.dto.SessionInfo;
import com.example.Insideout.dto.SessionResponse;
import com.example.Insideout.dto.UploadFileResponse;
import com.example.Insideout.service.ChatImageUploadService;
import com.example.Insideout.service.JwtUtil;
import com.example.Insideout.service.SessionService;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/chat")
public class SessionController {

    private final SessionService sessionService;
    private final ChatImageUploadService chatImageUploadService;
    private final JwtUtil jwtUtil;

    public SessionController(SessionService sessionService, ChatImageUploadService chatImageUploadService,
                             JwtUtil jwtUtil) {
        this.sessionService = sessionService;
        this.chatImageUploadService = chatImageUploadService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/session/create")
    public ResponseEntity<SessionResponse> createSession(@RequestHeader("Authorization") String token) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            return ResponseEntity.ok(sessionService.createNewSession(userId));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @DeleteMapping("/session/{sessionId}/delete")
    public ResponseEntity<String> deleteSession(@RequestHeader("Authorization") String token,
                                                @PathVariable Long sessionId) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            sessionService.deleteSession(userId, sessionId);
            return ResponseEntity.ok("세션 삭제 성공");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(
            summary = "유저의 세션 조회",
            description = "상담창 세션 조회, 관리자가 유저 세션 조회"
    )
    @GetMapping("/sessions")
    public ResponseEntity<List<SessionInfo>> getUserSessions(@RequestHeader("Authorization") String token) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            return ResponseEntity.ok(sessionService.getSessionsByUserId(userId));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{sessionId}/messages")
    public ResponseEntity<List<MessageResponse>> getSessionMessages(@RequestHeader("Authorization") String token,
                                                                    @PathVariable Long sessionId) {
        try {
            jwtUtil.validateToken(token);
            return ResponseEntity.ok(sessionService.getMessagesBySessionId(sessionId));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(
            description = "상담 세션 시작 시 ors 점수 전달"
    )
    @PutMapping("/ORS")
    public ResponseEntity<String> updateOrsScore(@RequestHeader("Authorization") String token,
                                                 @RequestBody ORSRequest request) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            sessionService.updateOrsScore(request, userId);
            return ResponseEntity.ok("ORS 점수 등록 성공");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(
            description = "세션 종료 srs점수, 동의 여부 업데이트 세션 상담 내용 요약 및 저장 (fast API 연결)"
    )
    @PutMapping("/session/terminate")
    public ResponseEntity<String> updateSessionDetails(@RequestHeader("Authorization") String token,
                                                       @RequestBody SessionEndRequest request) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            sessionService.endSession(request, userId);
            sessionService.SummarizeAndUpdateSession(request.getSessionId());
            return ResponseEntity.ok("세션 종료");
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * 프론트 입력 메세지 DB저장 -> fast API로 전달 -> 반환값 DB저장 -> 프론트로 반환
     */
    @PostMapping("/send")
    public ResponseEntity<MessageResponse> sendMessage(@RequestHeader("Authorization") String token,
                                                       @RequestBody MessageRequest messageRequest) {
        try {
            jwtUtil.validateToken(token);
            return ResponseEntity.ok(sessionService.processMessage(messageRequest));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/upload-image")
    public ResponseEntity<UploadFileResponse> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = chatImageUploadService.uploadImage(image);
            return ResponseEntity.ok(new UploadFileResponse(null, image.getOriginalFilename(), imageUrl, "이미지 업로드 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new UploadFileResponse(null, null, null, "이미지 업로드 실패: " + e.getMessage()));
        }
    }
}
