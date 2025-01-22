package com.example.Insideout.service;

import com.example.Insideout.entity.Board;
import com.example.Insideout.entity.UploadFile;
import com.example.Insideout.repository.BoardRepository;
import com.example.Insideout.repository.UploadFileRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service
public class UploadFileService {
    private final UploadFileRepository uploadFileRepository;
    private final BoardRepository boardRepository;
    private final RestTemplate restTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    @Value("${supabase.url}")
    private String supabaseUrl;

    @Value("${supabase.key}")
    private String supabaseKey;

    @Value("${supabase.bucket}")
    private String bucketName;


    public UploadFileService(UploadFileRepository uploadFileRepository, BoardRepository boardRepository,
                             RestTemplate restTemplate) {
        this.uploadFileRepository = uploadFileRepository;
        this.boardRepository = boardRepository;
        this.restTemplate = restTemplate;
    }

    // 파일 업로드 & 저장

    @Transactional
    public void uploadFile(MultipartFile multipartFile, Board board) {

        try {

            log.info("Received file upload request for board: {}", board);

            String mimeType = multipartFile.getContentType();
            log.info("MIME Type: {}", mimeType);

            if (mimeType == null || !mimeType.startsWith("image/")) {
                throw new RuntimeException("허용되지 않는 파일 형식입니다:" + mimeType);
            }

            //flush를 통한 board 저장
            if (board.getInquiryId() == null) {
                log.info("Board ID is null, saving new board");
                boardRepository.save(board);
                entityManager.flush(); // DB에 즉시 반영
                entityManager.clear(); //세션 초기화
            }

            // board를 다시 조회하여 trancational에 반영
            board = boardRepository.findById(board.getInquiryId())
                    .orElseThrow(() -> new RuntimeException("게시글 조회 실패"));
            log.info("Board retrieved: {}", board);

            //파일 저장 경로 생성
            String originalFileName = multipartFile.getOriginalFilename();
            if (originalFileName == null) {
                throw new RuntimeException("파일 이름을 확인할 수 없습니다.");
            }
            log.info("Original file name: {}", originalFileName);

            //파일 이름 변환 (허용되지 않는 문자 제거)
            String safeFileName = originalFileName.replaceAll("[^a-zA-Z0-9.]", "_");
            String uniqueFileName = UUID.randomUUID().toString() + "_" + safeFileName;
            log.info("Transformed safe file name: {}", safeFileName);
            log.info("Generated unique file name: {}", uniqueFileName);

            //supabase 업로드 URL 생성
            String uploadUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/" + uniqueFileName;
            log.info("Supabase Upload URL: {}", uploadUrl);

            //파일 업로드
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);
            headers.setContentType(MediaType.parseMediaType(mimeType));

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(multipartFile.getBytes(), headers);
            ResponseEntity<String> response = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity,
                    String.class);

            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.CREATED) {
                throw new RuntimeException("파일 업로드 실패: " + response.getBody());
            }
            log.info("File uploaded successfully to Supabase");

            // pulbic URL 생성
            String saveUrl = supabaseUrl + "/storage/v1/object/public/" + bucketName + "/" + uniqueFileName;
            log.info("Generated Public URL: {}", saveUrl);

            //Files 엔터티 생성 및 저장
            UploadFile uploadFile = new UploadFile();
            uploadFile.setBoard(board);
            uploadFile.setOriginalName(originalFileName);
            uploadFile.setSaveName(uniqueFileName);
            uploadFile.setFilePath(saveUrl);
            uploadFile.setUploadAt(LocalDateTime.now());

            uploadFileRepository.save(uploadFile);
            log.info("File metadata saved to database: {}", uploadFile);
        } catch (IOException e) {
            log.error("파일 업로드 중 IOException 발생: {}", e.getMessage(), e);
            throw new RuntimeException("파일 업로드 중 IOException 발생", e);
        } catch (Exception e) {
            log.error("파일 업로드 중 예기치 못한 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("파일 업로드 중 예기치 못한 오류 발생", e);
        }

    }

    //     파일 삭제
    @Transactional
    public void deleteUploadedFile(Long fileId) {
        try {
            UploadFile uploadFile = uploadFileRepository.findById(fileId)
                    .orElseThrow(() -> new RuntimeException("파일 조회 실패 "));

            //supabase 파일 삭제 URL 생성
            String deleteUrl = supabaseUrl + "/storage/v1/object/" + bucketName + "/"
                    + uploadFile.getSaveName(); //삭제시 url에 public이 들어가면 안되서 url 재생성
            log.info("supabase Delete URL: {}", deleteUrl);

            // supabase 파일 삭제 요청
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + supabaseKey);

            HttpEntity<Void> reqiesetEntity = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(deleteUrl, HttpMethod.DELETE, reqiesetEntity,
                    String.class);

            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new RuntimeException("Supabase 파일 삭제 실패: " + response.getBody());
            }
            log.info("Supabase 파일 삭제 성공: ", deleteUrl);

            uploadFileRepository.delete(uploadFile);
            log.info("데이터베이스에서 파일 정보 삭제 완료: {}", fileId);
        } catch (Exception e) {
            log.error("파일 삭제 중 오류 발생: {}", e.getMessage(), e);
            throw new RuntimeException("파일 삭제 중 오류 발생", e);
        }
    }

}
