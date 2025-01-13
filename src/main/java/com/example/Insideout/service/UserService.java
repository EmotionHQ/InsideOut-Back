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
    private final DepartmentService departmentService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       DepartmentService departmentService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.departmentService = departmentService;

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

        if ("MANAGER".equalsIgnoreCase(userDto.getRole())) {
            user.setDeptCode(departmentService.generateUniqueDeptCode());
        } else if ("USER".equalsIgnoreCase(userDto.getRole())) {
            String deptCode = user.getDeptCode();
            String department_name = departmentService.findDepartmentByDeptCode(deptCode);

            user.setDeptCode(deptCode);
            user.setDepartment(department_name);

        } else if ("ADMIN".equalsIgnoreCase(userDto.getRole())) {
            user.setDeptCode(null);
            user.setDepartment(null);
        }

        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
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
}