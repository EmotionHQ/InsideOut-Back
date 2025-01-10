package com.example.Insideout.service;

import com.example.Insideout.dto.UserDto;
import com.example.Insideout.entity.User;
import com.example.Insideout.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserDto userDto) {
        User user = new User();

        user.setUserId(userDto.getUserId());
        user.setPasswordHash(passwordEncoder.encode(userDto.getPasswordHash()));
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        user.setPhoneNumber(userDto.getPhoneNumber());
        user.setDepartment(userDto.getDepartment());
        user.setRole(User.Role.valueOf(userDto.getRole().toUpperCase()));
        user.setDeptCode(userDto.getDeptCode());
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }
}

