package com.example.Insideout.controller;

import com.example.Insideout.dto.BoardRequest;
import com.example.Insideout.dto.BoardResponse;
import com.example.Insideout.service.BoardService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    //공지사항 조회
    @GetMapping("/notice")
    public ResponseEntity<List<BoardResponse>> getNoticeBoard() {
        List<BoardResponse> responses = boardService.getNoticeBoards();
        return ResponseEntity.ok(responses);
    }

    //문의게시판 조회
    @GetMapping("/inquiry")
    public ResponseEntity<List<BoardResponse>> getInquiryBoard() {
        List<BoardResponse> responses = boardService.getInquiryBoards();
        return ResponseEntity.ok(responses);
    }


    // 작성
    @PostMapping("/create")
    public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest request) {
        BoardResponse response = boardService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
