package com.example.Insideout.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageRequest {
    private Long sessionId;
    private String content;
    private LocalDateTime createdAt;
    private String imageUrl;
}