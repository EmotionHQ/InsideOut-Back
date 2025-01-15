package com.example.Insideout.controller;

import com.example.Insideout.dto.BoardRequest;
import com.example.Insideout.dto.BoardResponse;
import com.example.Insideout.service.BoardService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    //공지사항 상세조회
    @GetMapping("/notice/{inquiryId}")
    private ResponseEntity<BoardResponse> getNoticeDetail(@PathVariable Long inquiryId) {
        BoardResponse response = boardService.getNoticeDetail(inquiryId);
        return ResponseEntity.ok(response);
    }

    //문의게시판 조회
    @GetMapping("/inquiry")
    public ResponseEntity<List<BoardResponse>> getInquiryBoard() {
        List<BoardResponse> responses = boardService.getInquiryBoards();
        return ResponseEntity.ok(responses);
    }

    // 문의 게시판 상세 조회
    @GetMapping("/inquiry/{inquiryId}")
    private ResponseEntity<BoardResponse> getInquiryDetail(@PathVariable Long inquiryId) {
        BoardResponse response = boardService.getInquiryDetail(inquiryId);
        return ResponseEntity.ok(response);
    }

    // 작성
    @PostMapping("/create")
    public ResponseEntity<BoardResponse> createBoard(@RequestBody BoardRequest request) {
        BoardResponse response = boardService.createPost(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 수정
    // pathVariable : url 경로에서 inquiryId 값 추출해서 inquiryId 변수에 매핑하는 역할
    // requestBody : 본문에 있는 json 데이터 BoardRequest 객체로 매핑
    @PutMapping("/modify/{inquiryId}")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable("inquiryId") Long inquiryId, @RequestBody BoardRequest request) {
        request.setInquiryId(inquiryId);
        BoardResponse response = boardService.updatePost(request);
        return ResponseEntity.ok(response);
    }
}
