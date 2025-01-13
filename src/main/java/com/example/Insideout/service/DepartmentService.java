package com.example.Insideout.service;

import com.example.Insideout.dto.DepartmentDto;
import com.example.Insideout.dto.UserDto;
import com.example.Insideout.entity.Department;
import com.example.Insideout.repository.DepartmentRepository;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    /**
     * 고유한 부서 코드 생성(중복 체크 포함)
     */
    public String generateUniqueDeptCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (departmentRepository.existsByDeptCode(code)); // 중복 체크
        return code;
    }

    /**
     * 랜덤 부서 코드 생성
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int n = random.nextInt(36);
            if (n < 10) {
                codeBuilder.append(n);
            } else {
                codeBuilder.append((char) (n + 55));
            }
        }
        return codeBuilder.toString();
    }

    /**
     * 부서 코드를 기반으로 부서명 조회
     */

    public String findDepartmentByDeptCode(String deptCode) {
        return departmentRepository.findByDeptCode(deptCode)
                .map(Department -> Department.getDepartment())
                .orElseThrow(() -> new IllegalArgumentException("Invalide department code"));
    }

    public Department saveDepartmentFromuserDto(UserDto userDto) {
        DepartmentDto departmentDto = new DepartmentDto();

        departmentDto.setDeptCode(userDto.getDeptCode());
        departmentDto.setDepartment(userDto.getDepartment());

        return saveDepartment(departmentDto);
    }


    public Department saveDepartment(DepartmentDto departmentDto) {
        Department department = new Department();

        department.setDeptCode(departmentDto.getDeptCode());
        department.setDepartment(departmentDto.getDepartment());

        return departmentRepository.save(department);
    }
}

