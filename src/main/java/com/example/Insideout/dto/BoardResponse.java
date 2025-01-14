package com.example.Insideout.dto;

import java.time.LocalDateTime;
import lombok.Getter;

// 제목,내용,유저아이디,문의 아이디,작성시간,수정시간,문구
@Getter
public class BoardResponse {

    private Long inquiryId;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private String message;

    public BoardResponse(Long inquiryId, String userId, String title, String content , String message, LocalDateTime createdTime, LocalDateTime modifiedTime) {
        this.inquiryId = inquiryId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdTime = LocalDateTime.now();
        this.modifiedTime = LocalDateTime.now();
        this.message = message;
    }
}


