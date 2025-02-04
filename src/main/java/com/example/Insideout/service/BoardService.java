package com.example.Insideout.service;
//작성,조회,수정,삭제
//작성 post, 그 외 get

import com.example.Insideout.dto.BoardRequest;
import com.example.Insideout.dto.BoardResponse;
import com.example.Insideout.dto.CommentResponse;
import com.example.Insideout.entity.Board;
import com.example.Insideout.entity.UploadFile;
import com.example.Insideout.entity.User;
import com.example.Insideout.repository.BoardRepository;
import com.example.Insideout.repository.CommentRepository;
import com.example.Insideout.repository.UploadFileRepository;
import com.example.Insideout.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private final CommentRepository commentRepository;

    public BoardService(BoardRepository boardRepository, UserRepository userRepository,
                        UploadFileService uploadFileService, UploadFileRepository uploadFileRepository,
                        CommentRepository commentRepository) {

        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.uploadFileService = uploadFileService;
        this.uploadFileRepository = uploadFileRepository;
        this.commentRepository = commentRepository;
    }

    /*
    공지사항 전체 조회
     */
    @Transactional
    public Page<BoardResponse> getNoticeBoards(String keyword, int page, int size) {
        List<Board> boards;

        if (keyword == null || keyword.trim().isEmpty()) {
            boards = boardRepository.findNoticeBoards();
        } else {
            boards = boardRepository.findNoticeBoardsByTitle(keyword);
        }

        // 전체 검색 결과에서 페이징 적용
        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), boards.size());
        List<Board> pageNotices = boards.subList(start, end);

        return new PageImpl<>(pageNotices, pageable, boards.size())
                .map(board -> new BoardResponse(
                        board.getInquiryId(),
                        board.getUserId(),
                        board.getTitle(),
                        "공지사항 조회 성공"
                ));
    }


    /*
    공지사항 상세 조회
     */
    public BoardResponse getNoticeDetail(Long inquiryId) {
        Board board = boardRepository.findNoticeBoardById(inquiryId)
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
    public Page<BoardResponse> getInquiryBoards(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Board> boards = boardRepository.findInquiryBoards(pageable);

        return boards.map(board -> {
            Long commentCount = commentRepository.countByInquiryId(board.getInquiryId());
            return new BoardResponse(
                    board.getInquiryId(),
                    board.getUserId(),
                    board.getTitle(),
                    commentCount,
                    "문의게시판 전체 조회 성공"
            );
        });
    }

    /*
    나의 문의 전체 조회
     */
    public Page<BoardResponse> getMyInquiryBoards(String userId, int page, int size) {
        List<Board> boards = boardRepository.findInquiryBoardsByMyPost(userId);

        Pageable pageable = PageRequest.of(page, size);
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), boards.size());
        List<Board> pagedInquiries = boards.subList(start, end);

        return new PageImpl<>(pagedInquiries, pageable, boards.size())
                .map(board -> {
                    Long commentCount = commentRepository.countByInquiryId(board.getInquiryId());
                    return new BoardResponse(
                            board.getInquiryId(),
                            board.getUserId(),
                            board.getTitle(),
                            commentCount,
                            "내 문의글 조회 성공"
                    );
                });
    }

    /*
    문의 상세 조회
     */
    @Transactional
    public BoardResponse getInquiryDetail(Long inquiryId) {
        Board board = boardRepository.findInquiryBoards(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의 게시글을 찾을 수 없습니다."));

//        User requester = userRepository.findById(request.getUserId())
//                .orElseThrow(() -> new IllegalArgumentException("해당 요청 유저를 찾을 수 없습니다."));
//
//        if (!board.getUserId().equals(request.getUserId()) && !requester.getRole().equals(User.Role.ADMIN)) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "게시글 조회 권한이 없습니다.");
//        }

        List<String> filePath = uploadFileRepository.findByBoard(board)
                .stream()
                .map(UploadFile::getFilePath)
                .toList();

        //문의 게시판 조회시 댓글 같이 조회
        //Board board = optionalBoard.get();
        List<CommentResponse> commentResponses = board.getComments() == null ?
                Collections.emptyList() :
                board.getComments().stream()
                        .map(comment -> {
                            // userId를 사용해 User 객체를 조회
                            User user = userRepository.findById(comment.getUserId())
                                    .orElseThrow(() -> new IllegalArgumentException("해당 사용자를 찾을 수 없습니다."));

                            return new CommentResponse(
                                    comment.getCommentId(),
                                    comment.getUserId(),
                                    user.getRole().toString(), // 조회된 user 객체에서 role 가져오기
                                    comment.getContent(),
                                    comment.getCreatedTime(),
                                    comment.getModifiedTime(),
                                    "댓글 조회 성공"
                            );
                        })
                        .toList();

        return new BoardResponse(
                board.getUserId(),
                board.getTitle(),
                board.getContent(),
                board.getCreatedTime(),
                board.getModifiedTime(),
                commentResponses,
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

        Board saveBoard = boardRepository.save(board);

        if (file != null && !file.isEmpty()) {
            try {
                uploadFileService.uploadFile(file, saveBoard);
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
        if (!board.getUserId().equals(request.getUserId())) {
            throw new IllegalArgumentException("게시글 수정 권한이 없습니다.");
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
            }
            // 새 파일 업로드 및 저장
            uploadFileService.uploadFile(file, board);
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

        return new BoardResponse("공지 게시글이 성공적으로 삭제되었습니다.");
    }

    // 문의 게시글 삭제
    public BoardResponse deleteInquiry(BoardRequest request) {
        Board board = boardRepository.findById(request.getInquiryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        User requester = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 요청 유저를 찾을 수 없습니다."));

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

        return new BoardResponse("게시글이 성공적으로 삭제되었습니다.");
    }

}