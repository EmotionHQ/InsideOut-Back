package com.example.Insideout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id", nullable = false)
    private Long fileId;

    @Column(name = "inquiry_id", nullable = false)
    private Long inquiryId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadAt;
}
