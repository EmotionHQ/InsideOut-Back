package com.example.Insideout.service;

import com.example.Insideout.entity.Board;
import com.example.Insideout.entity.UploadFile;
import com.example.Insideout.repository.BoardRepository;
import com.example.Insideout.repository.UploadFileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
public class UploadFileService {
    private final UploadFileRepository uploadFileRepository;
    private final BoardRepository boardRepository;
    private final String uploadDir = "C:/upload/";

    @PersistenceContext
    private EntityManager entityManager;

    public UploadFileService(UploadFileRepository uploadFileRepository, BoardRepository boardRepository) {
        this.uploadFileRepository = uploadFileRepository;
        this.boardRepository = boardRepository;
    }

    // 파일 업로드 & 저장

    @Transactional

    public void uploadFile(MultipartFile multipartFile, Board board) {

        try {
            //flush를 통한 board 저장
            if (board.getInquiryId() == null) {
                boardRepository.save(board);
                entityManager.flush(); // DB에 즉시 반영
                entityManager.clear(); //세션 초기화
            }

            // board를 다시 조회하여 trancational에 반영
            board = boardRepository.findById(board.getInquiryId())
                    .orElseThrow(() -> new RuntimeException("게시글 조회 실패"));

            //파일 저장 경로 생성
            String originalFileName = multipartFile.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            Path savePath = Paths.get(uploadDir + File.separator + uniqueFileName); //File.separator 윈도우 맥 둘다 호환

            // 디렉토리 생성(존재하지 않을 경우)
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            //파일 저장
            Files.copy(multipartFile.getInputStream(), savePath);

            //Files 엔터티 생성 및 저장
            UploadFile uploadFile = new UploadFile();
            uploadFile.setBoard(board);
            uploadFile.setOriginalName(originalFileName);
            uploadFile.setSaveName(uniqueFileName);
            uploadFile.setFilePath(savePath.toString());
            uploadFile.setUploadAt(LocalDateTime.now());

            uploadFileRepository.save(uploadFile);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생", e);
        }

    }


}
