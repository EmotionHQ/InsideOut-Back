package com.example.Insideout.service;

import com.example.Insideout.dto.CommentRequest;
import com.example.Insideout.dto.CommentResponse;
import com.example.Insideout.entity.Board;
import com.example.Insideout.entity.Comment;
import com.example.Insideout.entity.User;
import com.example.Insideout.repository.BoardRepository;
import com.example.Insideout.repository.CommentRepository;
import com.example.Insideout.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

// 필터링 :(댓글 작성자 = 게시글 작성자) userid 일치 여부 와 role = admin 인지.

@Service
public class CommentService {
    private final CommentRepository commentRepository;
    private UserRepository userRepository;
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

        // 권한 확인 (게시글 작성자 또는 ADMIN만 댓글 작성 가능)
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
                user.getRole().toString(),
                comment.getContent(),
                comment.getCreatedTime(),
                "댓글 작성 완료"
        );

    }

    /*
    댓글 삭제(문의글 답변)
    */
    public CommentResponse deleteComment(Long commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        if (!comment.getUserId().equals(userId) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 삭제 권한이 없습니다.");
        }
        commentRepository.delete(comment);

        return new CommentResponse("공지 게시글이 성공적으로 삭제되었습니다.");
    }

    /*
    댓글 수정(문의글 답변)
    */
    public CommentResponse updateComment(Long commentId, String userId, String updatedContent) {
        // 댓글 조회
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글을 찾을 수 없습니다."));

        // 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다."));

        // 권한 확인: 댓글 작성자 또는 ADMIN만 수정 가능
        if (!comment.getUserId().equals(userId) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 수정 권한이 없습니다.");
        }

        // 댓글 내용 수정
        comment.setContent(updatedContent);
        comment.setModifiedTime(LocalDateTime.now()); // 수정 시간

        // 수정 댓글 저장
        commentRepository.save(comment);

        // 수정된 댓글  반환
        return new CommentResponse(
                comment.getCommentId(),
                comment.getUserId(),
                user.getRole().toString(),
                comment.getContent(),
                comment.getCreatedTime(),
                comment.getModifiedTime(),
                "댓글이 성공적으로 수정되었습니다."
        );
    }
}


