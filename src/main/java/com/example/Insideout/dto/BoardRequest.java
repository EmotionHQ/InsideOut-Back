package com.example.Insideout.dto;

//작성 - 제목,내용,유저아이디

import lombok.Getter;

@Getter
public class BoardRequest {
    private String userId;
    private String title;
    private String content;
}
