package com.example.Insideout.service;

import com.example.Insideout.dto.UserDto;
import com.example.Insideout.entity.User;
import com.example.Insideout.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User registerUser(UserDto userDto) {
        User user = new User();

        user.setUserId(userDto.getUserId());
        user.setPasswordHash(userDto.getPasswordHash());
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

