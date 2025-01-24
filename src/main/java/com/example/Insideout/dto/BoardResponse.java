package com.example.Insideout.dto;

import com.example.Insideout.entity.Board;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
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
    private Board board;
    private List<String> filePath;
    private List<CommentResponse> comments; // 게시물 불러 올때 댓글 불러오기(리스트 형태로)

    // 작성 Dto
    public BoardResponse(Long inquiryId, String userId, String title, String content,
                         LocalDateTime createdTime, LocalDateTime modifiedTime, String message) {
        this.inquiryId = inquiryId;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
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
    public BoardResponse(Long inquiryId, String userId, String title, String message) {
        this.inquiryId = inquiryId;
        this.userId = userId;
        this.title = title;
        this.message = message;
    }

    // 삭제 Dto
    public BoardResponse(String message) {
        this.message = message;
    }

    //게시 상세 조회 Dto + 댓글 상세 조회 dto 추가
    public BoardResponse(String userId, String title, String content, LocalDateTime createdTime,
                         LocalDateTime modifiedTime, List<CommentResponse> comments, List<String> filePath, String message) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.filePath = filePath;
        this.comments = comments != null ? comments : Collections.emptyList(); // 댓글 리스트 처리
        this.message = message;
    }

    //공지 상세조회 dto
    public BoardResponse(String userId, String title, String content, LocalDateTime createdTime,
                         LocalDateTime modifiedTime, String message) {
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdTime = createdTime;
        this.modifiedTime = modifiedTime;
        this.message = message;
    }
  
    public Board getBoard() {
        return board;
    }
}


