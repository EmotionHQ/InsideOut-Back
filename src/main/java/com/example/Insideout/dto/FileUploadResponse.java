package com.example.Insideout.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FileUploadResponse {
    private Long fileId;
    private Long inquiryId;
    private String originalName;
    private String saveName;
    private String filePath;
}
