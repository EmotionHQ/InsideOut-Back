package com.example.Insideout.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDto {

    @NotBlank(message = "아이디는 필수 입력값입니다.")
    @Pattern(regexp = "^[a-z0-9]{4,12}$", message = "아이디는 영어 소문자와 숫자 4~12자리여야 합니다")
    private String userId;

    @NotBlank
    @Pattern(regexp = "^(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*(),.?\":{}|<>])[A-Za-z\\d!@#$%^&*(),.?\":{}|<>]{8,32}$",
            message = "비밀번호는 영문, 숫자, 특수문자를 각각 1개 이상 포함하고 8~32자여야 합니다.")
    private String passwordHash;

    private String name;

    @NotBlank(message = "이메일은 필수 입력값 입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$",
            message = "유효하지 않는 이메일 형식입니다.")
    private String email;

    private String phoneNumber;
    private String department;
    private String role;
    private String deptCode;

}

