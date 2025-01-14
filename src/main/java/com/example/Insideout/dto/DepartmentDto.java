package com.example.Insideout.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DepartmentDto {

    @NotBlank(message = "부서 코드 필요")
    private String deptCode;
    @NotBlank(message = "부서명 필요")
    private String department;
}
