package com.example.Insideout.service;

// 댓글 작성 , 댓글 수정(문의글 답변) , 문의글 답변 삭제

import com.example.Insideout.dto.CommentRequest;
import com.example.Insideout.dto.CommentResponse;
import com.example.Insideout.entity.Board;
import com.example.Insideout.entity.Comment;
import com.example.Insideout.entity.User;
import com.example.Insideout.repository.BoardRepository;
import com.example.Insideout.repository.CommentRepository;
import com.example.Insideout.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository,
                          BoardRepository boardRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.boardRepository = boardRepository;
    }

    /*
    댓글 작성
    */
    public CommentResponse addComment(Long inquiryId, CommentRequest request) {
        // 게시글 존재 확인
        Board board = boardRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        // 사용자 존재 확인
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

        // 권한 확인 (게시글 작성자 또는 관리자만 댓글 작성 가능)
        if (!board.getUserId().equals(request.getUserId()) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글을 작성할 권한이 없습니다.");
        }

        // 댓글 객체 생성 및 저장
        Comment comment = new Comment(request.getUserId(), request.getContent(), board);
        commentRepository.save(comment);

        // 댓글 작성 응답 반환
        return new CommentResponse(
                comment.getCommentId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getCreatedTime(),
                "댓글 작성 완료"
        );

    }

    /*
    댓글 삭제
    */
    public void deleteComment(Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (!comment.getUserId().equals(userId) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);
    }
}

