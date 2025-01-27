package com.example.Insideout.repository;

import com.example.Insideout.entity.Board;
import com.example.Insideout.entity.UploadFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UploadFileRepository extends JpaRepository<UploadFile, Long> {

    // 게시글에 연결된 파일 조회
    List<UploadFile> findByBoard(Board board);

}
