package com.example.Insideout.dto;

//작성 - 제목,내용,유저아이디

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRequest {
    private Long inquiryId;
    private String userId;
    private String title;
    private String content;

}
