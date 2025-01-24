package com.example.Insideout.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateDto {
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,32}$",
            message = "비밀번호는 대소문자, 숫자, 특수문자를 각각 1개 이상 포함하고 8~32자여야 합니다.")
    private String newPassword;
    
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "유효하지 않는 이메일 형식입니다.")
    private String email;
    
    private String phoneNumber;
    private String deptCode;
} 