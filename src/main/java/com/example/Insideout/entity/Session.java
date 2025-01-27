package com.example.Insideout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "session")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sessionId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private boolean isClosed = false;

    private Integer orsScore;

    private Integer srsScore;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Column(length = 255)
    private String feedback;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AgreementType agreement = AgreementType.DENIED;

    private String summary;

    public enum AgreementType {
        ACCEPTED, DENIED
    }

    public enum Status {
        RISK, STABLE
    }
}
