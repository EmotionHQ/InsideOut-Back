package com.example.Insideout.controller;

import com.example.Insideout.dto.CommentRequest;
import com.example.Insideout.dto.CommentResponse;
import com.example.Insideout.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // 댓글 작성
    @PostMapping("/{inquiryId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long inquiryId,
            @RequestBody CommentRequest request) {
        CommentResponse response = commentService.addComment(inquiryId, request);
        return ResponseEntity.ok(response);
    }
}
