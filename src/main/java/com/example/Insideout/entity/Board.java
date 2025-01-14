package com.example.Insideout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "Board")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_Id", nullable = false)
    private Long inquiryId; //문의 아이디

    @Column(name = "user_Id", nullable = false)
    private String userId; //유저 아이디

    @Column(name = "title", nullable = false)
    private String title; //제목

    @Column(length = 2000,name = "content", nullable = false)
    private String content; //내용

    @Column(name = "created_Time")
    private LocalDateTime createdTime;//작성시간

    @Column(name = "modified_Time")
    private LocalDateTime modifiedTime; // 수정시간

}
