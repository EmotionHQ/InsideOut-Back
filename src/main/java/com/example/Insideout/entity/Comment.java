package com.example.Insideout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

// 댓글 아이디,문의 아이디,유저 아이디,댓글 내용,작성 시간
// 필터링 : 다른 유저가 댓글 못 달도록 하기 (댓글 작성자 = 게시글 작성자) userid 일치 ,admin인지
@Entity
@Getter
@Setter
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private long commentId; //댓글아이디

    @Column(name = "inquiry_Id", nullable = false)
    private long inquiryId;

    @Column(name = "user_Id", nullable = false)
    private long userId;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "created_Time")
    private long createdTime;


}
