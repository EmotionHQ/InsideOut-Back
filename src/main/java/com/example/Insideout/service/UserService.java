package com.example.Insideout.service;

import com.example.Insideout.dto.UserDto;
import com.example.Insideout.dto.UserUpdateDto;
import com.example.Insideout.entity.Session;
import com.example.Insideout.entity.User;
import com.example.Insideout.repository.MessageRepository;
import com.example.Insideout.repository.SessionRepository;
import com.example.Insideout.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;
    private final DepartmentService departmentService;

    public UserService(UserRepository userRepository, SessionRepository sessionRepository,
                       MessageRepository messageRepository, PasswordEncoder passwordEncoder,
                       DepartmentService departmentService) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
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
        user.setRole(User.Role.valueOf(userDto.getRole().toUpperCase()));

        if ("MANAGER".equalsIgnoreCase(userDto.getRole())) {
            String deptCode = departmentService.generateUniqueDeptCode();

            user.setDeptCode(deptCode);
            userDto.setDeptCode(deptCode);

            departmentService.saveDepartmentFromUserDto(userDto);

        } else if ("USER".equalsIgnoreCase(userDto.getRole())) {
            String deptCode = userDto.getDeptCode();

            user.setDeptCode(deptCode);

        } else if ("ADMIN".equalsIgnoreCase(userDto.getRole())) {
            user.setDeptCode(null);
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

    public User findByUserId(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + userId));
    }

    public boolean verifyPassword(String userId, String password) {
        User user = findByUserId(userId);
        return passwordEncoder.matches(password, user.getPasswordHash());
    }

    public User updateUser(String userId, UserUpdateDto updateDto) {
        User user = findByUserId(userId);

        if (updateDto.getNewPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(updateDto.getNewPassword()));
        }
        if (updateDto.getEmail() != null) {
            user.setEmail(updateDto.getEmail());
        }
        if (updateDto.getPhoneNumber() != null) {
            user.setPhoneNumber(updateDto.getPhoneNumber());
        }
        if (updateDto.getDeptCode() != null && user.getRole() == User.Role.USER) {
            //String departmentName = departmentService.findDepartmentByDeptCode(updateDto.getDeptCode());
            user.setDeptCode(updateDto.getDeptCode());
            //user.setDepartment(departmentName);
        }

        return userRepository.save(user);
    }

    /**
     * 사용자 삭제 (연결된 세션, 메세지 삭제)
     */
    @Transactional
    public void deleteUserById(String userId) {
        if (!userRepository.existsByUserId(userId)) {
            throw new IllegalArgumentException("해당 유저가 존재하지 않습니다: " + userId);
        }

        List<Session> userSessions = sessionRepository.findAllByUserId(userId);

        if (!userSessions.isEmpty()) {
            messageRepository.deleteAllBySessionIn(userSessions);
            sessionRepository.deleteAll(userSessions);
        }

        userRepository.deleteByUserId(userId);
    }
}