package com.example.Insideout.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "departments")
public class Department {

    @Id
    @Column(name = "dept_code", nullable = false, unique = true)
    private String deptCode;

    @Column(name = "department_name", nullable = false, unique = true)
    private String department;

    @Column(name = "improvements", columnDefinition = "TEXT")
    private String improvements;  // 추가된 컬럼
}
