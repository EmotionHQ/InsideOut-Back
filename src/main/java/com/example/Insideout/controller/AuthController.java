package com.example.Insideout.controller;

import com.example.Insideout.dto.AuthRequest;
import com.example.Insideout.dto.AuthResponse;
import com.example.Insideout.entity.User;
import com.example.Insideout.service.JwtUtil;
import com.example.Insideout.service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    /**
     * 로그인 요청 처리
     *
     * @param authRequest 사용자 로그인 정보 (username, password)
     * @return JWT 토큰을 포함한 응답
     */
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserId(), authRequest.getPassword())
            );

            User user = userService.findByUserId(authRequest.getUserId());
            String jwt = jwtUtil.generateToken(user.getUserId());

            return new AuthResponse(
                    "로그인 성공",
                    user.getUserId(),
                    user.getName(),
                    jwt,
                    user.getRole().name()
            );

        } catch (org.springframework.security.authentication.BadCredentialsException ex) {
            // 인증 실패
            throw new RuntimeException("아이디 또는 비밀번호가 잘못되었습니다.", ex);
        } catch (Exception ex) {
            // 기타 예외 처리
            throw new RuntimeException("로그인 처리 중 문제가 발생했습니다.", ex);
        }
    }
}
