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

    // 수정 Dto
    public BoardResponse(String userId, String title, String content, LocalDateTime modifiedTime, String message) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.modifiedTime = modifiedTime;
        this.message = message;
    }

    // 전체 조회 Dto
    public BoardResponse(String userId, String title, String message) {
        this.userId = userId;
        this.title = title;
        this.message = message;
    }

    // 상세 조회 Dto
    public BoardResponse(String userId, String title, String content, LocalDateTime createdTime, LocalDateTime modifiedTime, String message) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.message = message;
    }

}


