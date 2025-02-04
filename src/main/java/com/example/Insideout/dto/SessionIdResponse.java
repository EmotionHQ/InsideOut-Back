package com.example.Insideout.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionIdResponse {
    private Long sessionId;
    private LocalDateTime createdAt;
}