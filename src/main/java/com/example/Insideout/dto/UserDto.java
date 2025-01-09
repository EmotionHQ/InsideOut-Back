package com.example.Insideout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    private String userId;
    private String passwordHash;
    private String name;
    private String email;
    private String phoneNumber;
    private String department;
    private String role;
    private String deptCode;
}

