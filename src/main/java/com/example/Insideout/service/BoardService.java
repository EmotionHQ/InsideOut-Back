package com.example.Insideout.service;
//작성,조회,수정,삭제
//작성 post, 그 외 get

import com.example.Insideout.dto.BoardRequest;
import com.example.Insideout.dto.BoardResponse;
import com.example.Insideout.entity.Board;
import com.example.Insideout.repository.BoardRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {

        this.boardRepository = boardRepository;
    }

    // 작성
    public BoardResponse createPost(BoardRequest request) {
        Board board = new Board(request);

        board.setCreatedTime(LocalDateTime.now());
        board.setModifiedTime(LocalDateTime.now());

        boardRepository.save(board);

        return new BoardResponse(
                board.getUserId(),
                board.getTitle(),
                board.getContent(),
                "게시글이 성공적으로 등록되었습니다."
        );
    }
}