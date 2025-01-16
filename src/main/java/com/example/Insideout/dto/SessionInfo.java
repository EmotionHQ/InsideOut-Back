package com.example.Insideout.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SessionInfo {
    private Long sessionId;
    private LocalDateTime createdAt;
    private Boolean isClosed;
}