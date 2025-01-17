package com.example.Insideout.repository;


import com.example.Insideout.entity.Board;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BoardRepository extends JpaRepository<Board, Long> {

    /*
     *Role에 따라 데이터 필터링 쿼리
     */
    @Query("SELECT b FROM Board b JOIN User u ON b.userId = u.userId WHERE u.role = 'ADMIN'")
    List<Board> findNoticeBoards();

    @Query("SELECT b FROM Board b JOIN User u ON b.userId = u.userId WHERE u.role IN ('USER','MANAGER')")
    List<Board> findInquiryBoards();
}
