package com.example.Insideout.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.Insideout.dto.UserDto;
import org.junit.jupiter.api.Test;

class UserDtoTest {

    @Test
    void testSetRole_ManagerRoleGeneratesDeptCode() {
        // Given
        UserDto userDto = new UserDto();

        // When
        userDto.setRole("MANAGER");

        // Then
        assertNotNull(userDto.getDeptCode(), "deptCode should not be null when role is MANAGER");
        assertEquals(6, userDto.getDeptCode().length(), "deptCode should have exactly 6 characters");
        assertTrue(userDto.getDeptCode().matches("^[A-Z0-9]{6}$"),
                "deptCode should only contain alphanumeric characters");
        System.out.println("RandomCode : " + userDto.getDeptCode());
    }

    @Test
    void testSetRole_NonManagerRoleDoesNotGenerateDeptCode() {
        // Given
        UserDto userDto = new UserDto();

        // When
        userDto.setRole("USER");

        // Then
        assertNull(userDto.getDeptCode(), "deptCode should be null when role is not MANAGER");
    }

    @Test
    void testSetRole_CaseInsensitiveForManagerRole() {
        // Given
        UserDto userDto = new UserDto();

        // When
        userDto.setRole("manager");

        // Then
        assertNotNull(userDto.getDeptCode(), "deptCode should not be null when role is 'manager' in lowercase");
        assertEquals(6, userDto.getDeptCode().length(), "deptCode should have exactly 6 characters");
    }

    @Test
    void testGenerateRandomDeptCode_GeneratesUniqueCodes() {
        // Given
        UserDto userDto = new UserDto();

        // When
        String code1 = userDto.generateRandomDeptCode();
        String code2 = userDto.generateRandomDeptCode();

        // Then
        assertNotEquals(code1, code2, "Generated deptCodes should be unique");
        assertEquals(6, code1.length(), "Each generated deptCode should have exactly 6 characters");
        assertEquals(6, code2.length(), "Each generated deptCode should have exactly 6 characters");
    }
}
