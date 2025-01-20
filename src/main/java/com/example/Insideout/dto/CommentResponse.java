package com.example.Insideout.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {  //응답
    private Long commentId;
    private String userId;
    private String content;
    private LocalDateTime createdTime;
    private String message;

    public CommentResponse(Long commentId, String userId, String content, LocalDateTime createdTime, String message) {
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
        this.createdTime = createdTime;
        this.message = message;
    }
}
