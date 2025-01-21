package com.example.Insideout.repository;

import com.example.Insideout.dto.DepartmentInfoResponse;
import com.example.Insideout.entity.Department;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface DepartmentRepository extends JpaRepository<Department, String> {

    Optional<Department> findByDeptCode(String deptCode);

    boolean existsByDeptCode(String deptCode);

    boolean existsByDepartment(String department);

    // 모든 부서 정보 + 각 부서의 매니저 이름
    @Query("SELECT new com.example.Insideout.dto.DepartmentInfoResponse(d.deptCode, d.department, u.name) " +
            "FROM Department d LEFT JOIN User u ON d.deptCode = u.deptCode AND u.role = 'MANAGER'")
    List<DepartmentInfoResponse> findAllDepartmentsWithManagers();

    Optional<Department> findByDepartment(String departmentName);
}
