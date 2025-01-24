package com.example.Insideout.dto;

import com.example.Insideout.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserInfoResponse {
    private String name;
    private String userId;
    private String email;
    private String phoneNumber;
    private User.Role role;

    public UserInfoResponse(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }
}