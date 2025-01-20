package com.example.Insideout.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponse {
    private Long commentId;
    private String userId;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private String message;


    // 댓글 수정
    public CommentResponse(Long commentId, String userId, String content, LocalDateTime createdTime,
                           LocalDateTime modifiedTime, String message) {
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.message = message;
    }

    // 댓글 삭제
    public CommentResponse(String message) {
        this.message = message;
    }

    // 댓글 작성
    public CommentResponse(Long commentId, String userId, String content, LocalDateTime createdTime, String message) {
        this.commentId = commentId;
        this.userId = userId;
        this.content = content;
        this.createdTime = createdTime;
        this.message = message;
    }
}
