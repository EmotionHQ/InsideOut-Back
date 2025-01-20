package com.example.Insideout.service;

import com.example.Insideout.entity.Board;
import com.example.Insideout.entity.UploadFile;
import com.example.Insideout.repository.UploadFileRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileService {
    private final UploadFileRepository uploadFileRepository;
    private final String uploadDir = "/Users/user/upload-dir";

    public UploadFileService(UploadFileRepository uploadFileRepository) {
        this.uploadFileRepository = uploadFileRepository;
    }

    // 파일 업로드 & 저장

    public void uploadFile(MultipartFile multipartFile, Board board) {
        try {
            //파일 저장 경로 생성
            String originalFileName = multipartFile.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFileName;
            Path savePath = Paths.get(uploadDir + uniqueFileName);

            // 디렉토리 생성(존재하지 않을 경우)
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            //파일 저장
            Files.copy(multipartFile.getInputStream(), savePath);

            //Files 엔터티 생성 및 저장
            UploadFile fileEntity = new UploadFile();
            fileEntity.setBoard(board);
            fileEntity.setOriginalName(originalFileName);
            fileEntity.setSaveName(uniqueFileName);
            fileEntity.setFilePath(savePath.toString());
            fileEntity.setUploadAt(LocalDateTime.now());
            uploadFileRepository.save(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("파일 업로드 중 오류 발생", e);
        }

    }


}
