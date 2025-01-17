package com.example.Insideout.dto;


import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileUploadRequest {
    private MultipartFile file; // 업로드할 파일
    private Long inquiryId;
}
