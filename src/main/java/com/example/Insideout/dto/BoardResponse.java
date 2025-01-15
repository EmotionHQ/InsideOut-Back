package com.example.Insideout.dto;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

// 제목,내용,유저아이디,문의 아이디,작성시간,수정시간,문구
@Getter
@Setter
public class BoardResponse {

    private Long inquiryId;
    private String userId;
    private String title;
    private String content;
    private LocalDateTime createdTime;
    private LocalDateTime modifiedTime;
    private String message;

    // 작성 Dto
    public BoardResponse(String userId, String title, String content, String message) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.message = message;
    }

    // 조회 Dto
    public BoardResponse(Long inquiryId, String userId, String title, String content, LocalDateTime createdTime,
                         String message) {
        this.inquiryId = inquiryId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.message = message;
    }
}


