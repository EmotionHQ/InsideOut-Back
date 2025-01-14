package com.example.Insideout.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

    @NotBlank(message = "아이디가 필요합니다")
    private String userId;

    @NotBlank(message = "패스워드가 필요합니다")
    private String password;
}
