package com.example.Insideout.entity;

import com.example.Insideout.dto.BoardRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Getter
@Setter
@NoArgsConstructor
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

    @Column(length = 2000, name = "content", nullable = false)
    private String content; //내용

    @Column(name = "created_Time")
    private LocalDateTime createdTime;//작성시간

    @Column(name = "modified_Time")
    private LocalDateTime modifiedTime; // 수정시간

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setBoard(this);
    }

    public Board(BoardRequest request) {
        this.title = request.getTitle();
        this.content = request.getContent();
        this.userId = request.getUserId();
    }
}
