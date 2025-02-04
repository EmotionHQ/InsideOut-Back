package com.example.Insideout.controller;

import com.example.Insideout.dto.BoardRequest;
import com.example.Insideout.dto.BoardResponse;
import com.example.Insideout.service.BoardService;
import com.example.Insideout.service.JwtUtil;
import com.example.Insideout.service.UploadFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@RestController
@RequestMapping("/api/boards")
public class BoardController {

    private final BoardService boardService;
    private final UploadFileService uploadFileService;
    private final ObjectMapper objectMapper;
    private final JwtUtil jwtUtil;

    public BoardController(BoardService boardService, UploadFileService uploadFileService, ObjectMapper objectMapper,
                           JwtUtil jwtUtil) {
        this.boardService = boardService;
        this.uploadFileService = uploadFileService;
        this.objectMapper = objectMapper;
        this.jwtUtil = jwtUtil;
    }

    //공지사항 조회
    @GetMapping("/notice")
    public ResponseEntity<Page<BoardResponse>> getNoticeBoard(@RequestParam(required = false) String keyword,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        Page<BoardResponse> responses = boardService.getNoticeBoards(keyword, page, size);
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
    public ResponseEntity<Page<BoardResponse>> getInquiryBoard(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Page<BoardResponse> responses = boardService.getInquiryBoards(page, size);
        return ResponseEntity.ok(responses);
    }

    // 내 문의글 조회
    @GetMapping("/inquiry/myPost")
    public ResponseEntity<Page<BoardResponse>> getMyInquiryBoard(@RequestHeader("Authorization") String token,
                                                                 @RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "10") int size) {
        String pureToken = token.replace("Bearer ", "");

        //JWT 검증 및 userId 추출
        if (!jwtUtil.validateToken(pureToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = jwtUtil.extractUserId(pureToken);

        Page<BoardResponse> responses = boardService.getMyInquiryBoards(userId, page, size);
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
    public ResponseEntity<BoardResponse> createBoard(@RequestPart("request") String request,
                                                     @RequestPart(value = "imageFile", required = false) MultipartFile file)
            throws IOException {

        log.info("About to call uploadFileService.uploadFile()");
        log.info("BoardRequest: {}", request);
        log.info("MultipartFile: {}", file);
        try {
            BoardRequest boardRequest = objectMapper.readValue(request, BoardRequest.class); // BoardRequest 객체로 변환
            log.info("BoardRequest Object: {}", boardRequest); // 요청 내용 로깅

            BoardResponse response = boardService.createPost(boardRequest, file);

            return ResponseEntity.ok(response);

        } catch (MaxUploadSizeExceededException e) { // 파일 크기 초과 예외 처리
            log.error("파일 크기 초과 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(new BoardResponse("파일 크기가 너무 큽니다."));
        } catch (DataIntegrityViolationException e) { // 데이터 무결성 제약 조건 위반 예외 처리 (예: 중복 데이터)
            log.error("데이터 무결성 위반 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new BoardResponse("데이터 저장 중 오류가 발생했습니다. (데이터 중복 등)"));
        } catch (HttpMessageNotReadableException e) { // RequestBody 파싱 실패 예외 처리
            log.error("요청 본문 파싱 오 류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BoardResponse("요청 형식이 올바르지 않습니다. (JSON 파싱 오류 등)"));
        } catch (ConstraintViolationException e) {
            log.error("유효성 검사 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BoardResponse("요청 형식이 올바르지 않습니다."));
        } catch (RuntimeException e) { // 그 외 RuntimeException 처리
            log.error("게시글 생성 중 런타임 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BoardResponse("게시글 생성 중 오류가 발생했습니다."));
        } catch (Exception e) { // 그 외 모든 예외 처리 (최대한 구체적인 예외를 먼저 처리하는 것이 좋음)
            log.error("게시글 생성 중 일반 오류: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new BoardResponse("서버 오류가 발생했습니다."));
        }
    }

    // 수정
    // pathVariable : url 경로에서 inquiryId 값 추출해서 inquiryId 변수에 매핑하는 역할
    // requestBody : 본문에 있는 json 데이터 BoardRequest 객체로 매핑
    @PutMapping("/modify/{inquiryId}")
    public ResponseEntity<BoardResponse> updateBoard(@PathVariable("inquiryId") Long inquiryId,
                                                     @RequestPart("request") String request,
                                                     @RequestPart(value = "imagefile", required = false) MultipartFile file) {
        try {
            log.info("Raw request: {}", request);
            BoardRequest boardRequest = objectMapper.readValue(request, BoardRequest.class);

            boardRequest.setInquiryId(inquiryId);

            BoardResponse response = boardService.updatePost(boardRequest, file);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("JSON 파싱 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("예외 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 공지글 삭제
    @DeleteMapping("/notice/delete")
    public ResponseEntity<BoardResponse> deleteBoard(@RequestBody BoardRequest request) {
        BoardResponse response = boardService.deleteNotice(request);
        return ResponseEntity.ok(response);
    }

    // 문의 게시글 삭제
    @DeleteMapping("/inquiry/delete")
    public ResponseEntity<BoardResponse> deleteInquiry(@RequestBody BoardRequest request) {
        log.info("Delete Inquiry 요청 데이터: userId={}, inquiryId={}", request.getUserId(), request.getInquiryId());
        BoardResponse response = boardService.deleteInquiry(request);
        return ResponseEntity.ok(response);
    }

    //파일 삭제
    @DeleteMapping("/{fileId}")
    public ResponseEntity<String> deleteFile(@PathVariable Long fileId) {
        uploadFileService.deleteUploadedFile(fileId);
        return ResponseEntity.ok("삭제완료");
    }
}
