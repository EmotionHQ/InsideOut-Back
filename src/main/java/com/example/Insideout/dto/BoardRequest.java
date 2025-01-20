package com.example.Insideout.dto;

//작성 - 제목,내용,유저아이디

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class BoardRequest {
    private Long inquiryId;
    private String userId;
    private String title;
    private String content;
    private String filePath; // 파일 경로
    private MultipartFile imageFile;

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


}
