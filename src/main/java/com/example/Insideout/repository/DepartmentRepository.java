package com.example.Insideout.repository;

import com.example.Insideout.entity.Department;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, String> {

    Optional<Department> findByDeptCode(String deptCode);

    boolean existsByDeptCode(String deptCode);
}
