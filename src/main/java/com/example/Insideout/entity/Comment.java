package com.example.Insideout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 댓글 아이디,문의 아이디,유저 아이디,댓글 내용,작성 시간
@Entity
@Getter
@NoArgsConstructor
@Setter

@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", nullable = false)
    private Long commentId; //댓글아이디

    // 댓글과 게시글 다대일 관계
    // fetch = FetchType.LAZY : 지연 로딩(성능 최적화를 위해)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_Id", nullable = false)
    private Board board; // 게시글 아이디 (연관 관계)

    @Column(name = "user_Id", nullable = false)
    private String userId; //유저 아이디

    @Column(length = 1000, name = "content", nullable = false)
    private String content; //내용

    @Column(name = "created_Time")
    private LocalDateTime createdTime; //작성 시간

    @Column(name = "modified_Time")
    private LocalDateTime modifiedTime; // 수정시간

    public Comment(String userId, String content, Board board) {
        this.board = board;
        this.userId = userId;
        this.content = content;
        this.createdTime = LocalDateTime.now();
    }


}
