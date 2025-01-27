package com.example.Insideout.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordVerificationDto {
    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    private String password;
} 