package com.example.Insideout.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileResponse {
    private Long fileId;
    private String originalName;
    private String filePath;
    private String message;

}
