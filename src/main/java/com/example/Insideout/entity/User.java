package com.example.Insideout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

    @Id
    @Column(name = "user_id", nullable = false)
    private String userId; // Primary Key

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "phonenumber", nullable = true)
    private String phoneNumber;

    @Column(name = "department", nullable = true)
    private String department;

    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role; // Enum 타입

    @Column(name = "dept_code", nullable = true)
    private String deptCode;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Enum for Role
    public enum Role {
        ADMIN,
        USER,
        MANAGER
    }
}