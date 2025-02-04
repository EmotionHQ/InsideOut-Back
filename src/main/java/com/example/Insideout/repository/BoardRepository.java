package com.example.Insideout.repository;


import com.example.Insideout.entity.Board;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {

    /*
     *Role에 따라 데이터 필터링 쿼리 + 페이지네이션
     */
    // 공지 전체조회
    @Query("SELECT b FROM Board b WHERE b.userId IN (SELECT u.userId FROM User u WHERE u.role = 'ADMIN') ORDER BY b.createdTime DESC")
    List<Board> findNoticeBoards();

    //공지 검색
    @Query("SELECT b FROM Board b WHERE b.userId IN (SELECT u.userId FROM User u WHERE u.role = 'ADMIN') AND b.title LIKE %:keyword% ORDER BY b.createdTime DESC")
    List<Board> findNoticeBoardsByTitle(String keyword);

    // 공지 상세조회
    @Query("SELECT b FROM Board b JOIN User u ON b.userId = u.userId WHERE u.role = 'ADMIN' AND b.id = :inquiryId")
    Optional<Board> findNoticeBoardById(Long inquiryId);

    // 문의 전체조회
    @Query("SELECT b FROM Board b JOIN User u ON b.userId = u.userId WHERE u.role IN ('USER','MANAGER') ORDER BY b.createdTime DESC")
    Page<Board> findInquiryBoards(Pageable pageable);

    // 나의 문의 조회
    @Query("SELECT b FROM Board b WHERE b.userId = :userId AND b.userId IN (SELECT u.userId FROM User u WHERE u.role IN ('USER','MANAGER')) ORDER BY b.createdTime DESC")
    Page<Board> findInquiryBoardsByMyPost(String userId, Pageable pageable);

    // 문의 상세조회
    @Query("SELECT b FROM Board b JOIN User u ON b.userId = u.userId WHERE u.role IN ('USER','MANAGER') AND b.id = :inquiryId")
    Optional<Board> findInquiryBoards(Long inquiryId);

}
