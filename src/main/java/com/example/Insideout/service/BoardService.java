package com.example.Insideout.service;
//작성,조회,수정,삭제
//작성 post, 그 외 get

import com.example.Insideout.dto.BoardRequest;
import com.example.Insideout.dto.BoardResponse;
import com.example.Insideout.entity.Board;
import com.example.Insideout.entity.UploadFile;
import com.example.Insideout.entity.User;
import com.example.Insideout.repository.BoardRepository;
import com.example.Insideout.repository.UploadFileRepository;
import com.example.Insideout.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final UploadFileService uploadFileService;
    private final UploadFileRepository uploadFileRepository;

    public BoardService(BoardRepository boardRepository, UserRepository userRepository,
                        UploadFileService uploadFileService, UploadFileRepository uploadFileRepository) {

        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.uploadFileService = uploadFileService;
        this.uploadFileRepository = uploadFileRepository;
    }

    /*
    공지사항 전체 조회
     */
    public List<BoardResponse> getNoticeBoards() {
        List<Board> boards = boardRepository.findNoticeBoards();

        return boards.stream()
                .map(board -> new BoardResponse(
                        board.getUserId(),
                        board.getTitle(),
                        "공지사항 전체 조회 성공"
                ))
                .toList();
    }

    /*
    공지사항 상세 조회
     */
    public BoardResponse getNoticeDetail(Long inquiryId) {
        Board board = boardRepository.findNoticeBoards()
                .stream()
                .filter(b -> b.getInquiryId().equals(inquiryId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("공지사항 게시글을 찾을 수 없습니다."));

        List<String> filePath = uploadFileRepository.findByBoard(board)
                .stream()
                .map(UploadFile::getFilePath)
                .toList();

        return new BoardResponse(
                board.getUserId(),
                board.getTitle(),
                board.getContent(),
                board.getCreatedTime(),
                board.getModifiedTime(),
                filePath,
                "공지사항 상세조회 성공"
        );
    }


    /*
    문의 게시판 전체 조회
     */
    public List<BoardResponse> getInquiryBoards() {
        List<Board> boards = boardRepository.findInquiryBoards();

        return boards.stream()
                .map(board -> new BoardResponse(
                        board.getUserId(),
                        board.getTitle(),
                        "문의게시판 전체 조회 성공"
                ))
                .toList();
    }

    /*
    문의 상세 조회
     */
    public BoardResponse getInquiryDetail(BoardRequest request) {
        Board board = boardRepository.findInquiryBoards()
                .stream()
                .filter(b -> b.getInquiryId().equals(request.getInquiryId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("문의 게시글을 찾을 수 없습니다."));

        User requester = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 요청 유저를 찾을 수 없습니다."));

        log.info("조회 요청 유저: {}, 역할: {}, 유저:{}", request.getUserId(), requester.getRole(), requester);
        log.info("게시글 작성자: {}", board.getUserId());
        if (!board.getUserId().equals(request.getUserId()) && !requester.getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 조회 권한이 없습니다.");
        }

        List<String> filePath = uploadFileRepository.findByBoard(board)
                .stream()
                .map(UploadFile::getFilePath)
                .toList();

        return new BoardResponse(
                board.getUserId(),
                board.getTitle(),
                board.getContent(),
                board.getCreatedTime(),
                board.getModifiedTime(),
                filePath,
                "문의 게시글 상세조회 성공"
        );

    }

    // 작성
    @Transactional
    public BoardResponse createPost(BoardRequest request, MultipartFile file) {
        Board board = new Board(request);
        board.setUserId(request.getUserId());
        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setCreatedTime(LocalDateTime.now());
        board.setModifiedTime(null);

        log.info("Saving board: {}", board);

        Board saveBoard = boardRepository.save(board);
        log.info("Board saved: {}", saveBoard);

        if (file != null && !file.isEmpty()) {
            log.info("About to upload file: {}", file.getOriginalFilename()); // 파일 업로드 시작 전 로그
            try {
                uploadFileService.uploadFile(file, saveBoard);
                log.info("File uploaded successfully."); // 파일 업로드 성공 후 로그
            } catch (Exception e) {
                log.error("File upload failed: {}", e.getMessage(), e);
            }


        }

        return new BoardResponse(
                saveBoard.getInquiryId(),
                saveBoard.getUserId(),
                saveBoard.getTitle(),
                saveBoard.getContent(),
                saveBoard.getCreatedTime(),
                saveBoard.getModifiedTime(),
                "게시글이 성공적으로 등록되었습니다."
        );
    }

    // 공지 게시글 수정
    @Transactional
    public BoardResponse updatePost(BoardRequest request, MultipartFile file) {
        Board board = boardRepository.findById(request.getInquiryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        User requester = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 요청 유저를 찾을 수 없습니다."));

        if (!requester.getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공지글 수정 권한이 없습니다.");
        }

        board.setTitle(request.getTitle());
        board.setContent(request.getContent());
        board.setModifiedTime(LocalDateTime.now());

        boardRepository.save(board);

        if (file != null && !file.isEmpty()) {
            List<UploadFile> existingFiles = uploadFileRepository.findByBoard(board);

            if (existingFiles != null && !existingFiles.isEmpty()) {
                for (UploadFile files : existingFiles) {
                    uploadFileService.deleteUploadedFile(files.getFileId());
                }
            } else {
                log.info("기존 파일이 없습니다.");
            }
            // 새 파일 업로드 및 저장
            uploadFileService.uploadFile(file, board);
            log.info("File upload Successful");
        }

        return new BoardResponse(
                board.getUserId(),
                board.getTitle(),
                board.getContent(),
                board.getModifiedTime(),
                "공지 게시글이 성공적으로 수정되었습니다."
        );
    }

    // 공지 게시글 삭제
    public BoardResponse deleteNotice(BoardRequest request) {
        Board board = boardRepository.findById(request.getInquiryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        User requester = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 요청 유저를 찾을 수 없습니다."));

        if (!requester.getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "공지글 삭제 권한이 없습니다.");
        }

        List<UploadFile> existingFiles = uploadFileRepository.findByBoard(board);
        if (existingFiles != null && !existingFiles.isEmpty()) {
            for (UploadFile files : existingFiles) {
                try {
                    uploadFileService.deleteUploadedFile(files.getFileId());
                } catch (Exception e) {
                    log.error("파일 삭제 실패: fileId={}, error={}", files.getFileId(), e.getMessage(), e);
                }
            }
        }

        boardRepository.delete(board);
        log.info("게시글 삭제 완료: inquiryId={}", request.getInquiryId());

        return new BoardResponse("공지 게시글이 성공적으로 삭제되었습니다.");
    }

    // 문의 게시글 삭제
    public BoardResponse deleteInquiry(BoardRequest request) {
        Board board = boardRepository.findById(request.getInquiryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        User requester = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 요청 유저를 찾을 수 없습니다."));

        log.info("삭제 요청 유저: {}, 역할: {}, 유저:{}", request.getUserId(), requester.getRole(), requester);
        log.info("게시글 작성자: {}", board.getUserId());
        if (!board.getUserId().equals(request.getUserId()) && !requester.getRole().equals(User.Role.ADMIN)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 삭제 권한이 없습니다.");
        }

        List<UploadFile> existingFiles = uploadFileRepository.findByBoard(board);
        if (existingFiles != null && !existingFiles.isEmpty()) {
            for (UploadFile files : existingFiles) {
                try {
                    uploadFileService.deleteUploadedFile(files.getFileId());
                } catch (Exception e) {
                    log.error("파일 삭제 실패: fileId={}, error={}", files.getFileId(), e.getMessage(), e);
                }
            }
        }

        boardRepository.delete(board);
        log.info("게시글 삭제 완료: inquiryId={}", request.getInquiryId());

        return new BoardResponse("게시글이 성공적으로 삭제되었습니다.");
    }

}