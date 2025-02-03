package com.example.Insideout.controller;

import com.example.Insideout.dto.MessageRequest;
import com.example.Insideout.dto.MessageResponse;
import com.example.Insideout.dto.ORSRequest;
import com.example.Insideout.dto.SessionCreationRequest;
import com.example.Insideout.dto.SessionEndRequest;
import com.example.Insideout.dto.SessionInfo;
import com.example.Insideout.dto.SessionResponse;
import com.example.Insideout.dto.UploadFileResponse;
import com.example.Insideout.service.SessionService;
import com.example.Insideout.service.ChatImageUploadService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/chat")
public class SessionController {

    private final SessionService sessionService;
    private final ChatImageUploadService chatImageUploadService;

    public SessionController(SessionService sessionService, ChatImageUploadService chatImageUploadService) {
        this.sessionService = sessionService;
        this.chatImageUploadService = chatImageUploadService;
    }

    @PostMapping("/session/create")
    public SessionResponse createSession(@RequestBody SessionCreationRequest requestDTO) {
        return sessionService.createNewSession(requestDTO);
    }

    @DeleteMapping("/session/{sessionId}/delete")
    public ResponseEntity<String> deleteSession(@PathVariable Long sessionId) {
        sessionService.deleteSession(sessionId);
        return ResponseEntity.ok("Session deleted successfully.");
    }


    @GetMapping("/sessions")
    public List<SessionInfo> getUserSessions(@RequestParam String userId) {
        return sessionService.getSessionsByUserId(userId);
    }

    @GetMapping("/{sessionId}/messages")
    public List<MessageResponse> getSessionMessages(@PathVariable Long sessionId) {
        return sessionService.getMessagesBySessionId(sessionId);
    }

    @PutMapping("/ORS")
    public ResponseEntity<String> updateOrsScore(@RequestBody ORSRequest request) {
        sessionService.updateOrsScore(request.getSessionId(), request.getOrsScore());
        return ResponseEntity.ok("ORS score updated successfully.");
    }

    /**
     * 세션 종료 srs점수, 동의 여부 업데이트 세션 상담 내용 요약 및 저장 (fast API 연결)
     */
    @PutMapping("/session/terminate")
    public ResponseEntity<String> updateSessionDetails(@RequestBody SessionEndRequest request) {
        sessionService.endSession(request.getSessionId(), request.getSrsScore(), request.getAgreement());
        sessionService.SummarizeAndUpdateSession(request.getSessionId());
        return ResponseEntity.ok("Session details updated successfully.");
    }

    /**
     * 프론트 입력 메세지 DB저장 -> fast API로 전달 -> 반환값 DB저장 -> 프론트로 반환
     */
    @PostMapping("/send")
    public MessageResponse sendMessage(@RequestBody MessageRequest messageRequest) {
        return sessionService.processMessage(messageRequest);
    }

    @PostMapping("/upload-image")
    public ResponseEntity<UploadFileResponse> uploadImage(@RequestParam("image") MultipartFile image) {
        try {
            String imageUrl = chatImageUploadService.uploadImage(image);
            return ResponseEntity.ok(new UploadFileResponse(null, image.getOriginalFilename(), imageUrl, "이미지 업로드 성공"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new UploadFileResponse(null, null, null, "이미지 업로드 실패: " + e.getMessage()));
        }
    }
}
