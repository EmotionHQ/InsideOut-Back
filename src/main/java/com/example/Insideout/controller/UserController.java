package com.example.Insideout.controller;

import com.example.Insideout.dto.UserDto;
import com.example.Insideout.dto.PasswordVerificationDto;
import com.example.Insideout.dto.UserUpdateDto;
import com.example.Insideout.dto.UserInfoDto;
import com.example.Insideout.entity.User;
import com.example.Insideout.service.UserService;
import com.example.Insideout.service.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
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

    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody PasswordVerificationDto verificationDto,
            @RequestHeader("Authorization") String token) {
        try {
            String userId = jwtUtil.extractUserId(token.substring(7));
            boolean isValid = userService.verifyPassword(userId, verificationDto.getPassword());
            
            if (isValid) {
                return ResponseEntity.ok().body(Map.of("message", "비밀번호가 확인되었습니다."));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "비밀번호가 일치하지 않습니다."));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "비밀번호 확인 중 오류가 발생했습니다."));
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getUserInfo(@RequestHeader("Authorization") String token) {
        try {
            String userId = jwtUtil.extractUserId(token.substring(7));
            User user = userService.findByUserId(userId);
            
            UserInfoDto userInfo = new UserInfoDto();
            userInfo.setUserId(user.getUserId());
            userInfo.setName(user.getName());
            userInfo.setEmail(user.getEmail());
            userInfo.setPhoneNumber(user.getPhoneNumber());
            userInfo.setRole(user.getRole().name());
            userInfo.setDeptCode(user.getDeptCode());
            
            return ResponseEntity.ok(userInfo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "사용자 정보 조회 중 오류가 발생했습니다."));
        }
    }

    @PutMapping("/me")
    public ResponseEntity<?> updateUserInfo(@RequestBody UserUpdateDto updateDto,
            @RequestHeader("Authorization") String token) {
        try {
            String userId = jwtUtil.extractUserId(token.substring(7));
            User updatedUser = userService.updateUser(userId, updateDto);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "사용자 정보 수정 중 오류가 발생했습니다."));
        }
    }
}

