package com.example.Insideout.controller;

import com.example.Insideout.dto.SessionCreationRequest;
import com.example.Insideout.dto.SessionResponse;
import com.example.Insideout.service.SessionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/session/create")
    public ResponseEntity<SessionResponse> createSession(@RequestBody SessionCreationRequest requestDTO) {
        SessionResponse responseDTO = sessionService.createNewSession(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }
}
