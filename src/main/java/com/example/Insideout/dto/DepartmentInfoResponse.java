package com.example.Insideout.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DepartmentInfoResponse {
    private String deptCode;
    private String departmentName;
    private String managerName;
}