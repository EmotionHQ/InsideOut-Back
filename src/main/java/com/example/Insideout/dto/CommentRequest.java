//요청
package com.example.Insideout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequest {
    @Getter
    private Long commentId; // 댓글 아이디
    private Long inquiryId; // 게시글아이디
    private String userId; //유저 아이디
    private String content; // 댓글 내용
    private String message; //메세지

}
