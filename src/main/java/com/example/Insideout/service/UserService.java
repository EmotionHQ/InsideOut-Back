package com.example.Insideout.service;

import com.example.Insideout.dto.UserDto;
import com.example.Insideout.entity.User;
import com.example.Insideout.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserDto userDto) {
        try {
            if (userRepository.findById(userDto.getUserId()).isPresent()) {
                throw new RuntimeException("이미 존재하는 사용자 ID입니다.");
            }

            User user = new User();
            user.setUserId(userDto.getUserId());
            user.setPasswordHash(passwordEncoder.encode(userDto.getPasswordHash()));
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            user.setPhoneNumber(userDto.getPhoneNumber());
            user.setDepartment(userDto.getDepartment());
            
            // Role 설정 및 MANAGER인 경우 deptCode 생성
            User.Role role = User.Role.valueOf(userDto.getRole().toUpperCase());
            user.setRole(role);
            if (role == User.Role.MANAGER) {
                user.setDeptCode(userDto.generateRandomDeptCode());
            } else {
                user.setDeptCode(userDto.getDeptCode());  // MANAGER가 아닐 경우 사용자가 보낸 값 사용
            }
            
            user.setCreatedAt(LocalDateTime.now());

            return userRepository.save(user);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("잘못된 role 값입니다: " + userDto.getRole());
        } catch (Exception e) {
            throw new RuntimeException("회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * 사용자 아이디로 사용자 세부 정보 로드, 사용자 정보가 없을 경우 예외 발생
     */
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUserId())
                .password(user.getPasswordHash())
                .authorities(user.getRole().name())
                .build();
    }

    public User findByUserId(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }
}