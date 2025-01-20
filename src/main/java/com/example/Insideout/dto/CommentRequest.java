//요청
package com.example.Insideout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    private Long commentId;
    private Long inquiryId; // 게시글아이디
    private String userId; //유저 아이디
    private String content; // 댓글 내용
    private String message;

    public Long getCommentId() {
        return commentId;
    }

}
