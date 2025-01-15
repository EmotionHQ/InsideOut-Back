package com.example.Insideout.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SessionResponse {
    private Long sessionId;
    private String userId;
    private LocalDateTime createdAt;
}
