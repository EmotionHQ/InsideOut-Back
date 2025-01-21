package com.example.Insideout.controller;

import com.example.Insideout.dto.UserInfoResponse;
import com.example.Insideout.service.UserService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage")
public class ManageController {

    private final UserService userService;

    public ManageController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/department")
    public ResponseEntity<List<UserInfoResponse>> getUsersInSameDepartment(@RequestParam String userId) {
        List<UserInfoResponse> users = userService.getUsersInSameDepartment(userId);
        return ResponseEntity.ok(users);
    }
}