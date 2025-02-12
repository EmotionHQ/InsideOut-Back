package com.example.Insideout.passwordhash;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.Insideout.dto.UserDto;
import com.example.Insideout.entity.User;
import com.example.Insideout.repository.UserRepository;
import com.example.Insideout.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegisterUser() {
        UserDto userDto = new UserDto();
        userDto.setUserId("testUser");
        userDto.setPasswordHash("plainPassword");
        userDto.setName("Test User");
        userDto.setEmail("test@example.com");
        userDto.setPhoneNumber("123456789");
        userDto.setDepartment("IT");
        userDto.setRole("USER");
        userDto.setDeptCode("IT001");

        User user = userService.registerUser(userDto);

        assertThat(user).isNotNull();
        assertThat(user.getPasswordHash()).isNotEqualTo("plainPassword");
        assertThat(passwordEncoder.matches("plainPassword", user.getPasswordHash())).isTrue();

        System.out.println("dk" + user.getPasswordHash());
    }
}
