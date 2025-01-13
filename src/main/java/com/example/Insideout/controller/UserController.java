package com.example.Insideout.controller;

import com.example.Insideout.dto.UserDto;
import com.example.Insideout.entity.User;
import com.example.Insideout.service.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
        try {
            User savedUser = userService.registerUser(userDto);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);

        } catch (IllegalArgumentException e) {
            // 잘못된 입력 예외 처리
            return ResponseEntity.badRequest().body(
                    Map.of("error", e.getMessage())
            );
        } catch (RuntimeException e) {
            // 기타 런타임 예외 처리
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", "사용자 등록 중 문제가 발생했습니다.")
            );
        }
    }
}

