package com.example.Insideout.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthResponse {
    private String message;
    private String userId;
    private String name;
    private String jwt;
    private String role;

    public AuthResponse() {
    }

    public AuthResponse(String message, String userId, String name, String jwt, String role) {
        this.message = message;
        this.userId = userId;
        this.name = name;
        this.jwt = jwt;
        this.role = role;
    }
}
