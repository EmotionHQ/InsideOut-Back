package com.example.Insideout.controller;

import com.example.Insideout.dto.DepartmentInfoResponse;
import com.example.Insideout.dto.OrsStatisticsResponse;
import com.example.Insideout.dto.SessionIdResponse;
import com.example.Insideout.dto.SrsResponse;
import com.example.Insideout.dto.SrsStatisticsResponse;
import com.example.Insideout.dto.UserInfoResponse;
import com.example.Insideout.service.DepartmentService;
import com.example.Insideout.service.SessionService;
import com.example.Insideout.service.UserService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage")
public class ManageController {

    private final UserService userService;
    private final SessionService sessionService;
    private final DepartmentService departmentService;

    public ManageController(UserService userService, SessionService sessionService,
                            DepartmentService departmentService) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.departmentService = departmentService;
    }

    @GetMapping(value = "/department/users", params = "userId")
    public List<UserInfoResponse> getUsersInSameDepartment(@RequestParam String userId) {
        return departmentService.getUsersInSameDepartment(userId);
    }

    @GetMapping(value = "/department/users", params = "departmentName")
    public List<UserInfoResponse> getUsersByDepartmentName(@RequestParam String departmentName) {
        return departmentService.getUsersByDepartmentName(departmentName);
    }

    @GetMapping("/accepted")
    public List<SessionIdResponse> getAcceptedSessions(@RequestParam String userId) {
        return sessionService.getAcceptedSessionsByUserId(userId);
    }

    @GetMapping("/departments")
    public List<DepartmentInfoResponse> getAllDepartmentsInfo() {
        return departmentService.getAllDepartmentInfo();
    }

    @GetMapping("/statistics/ors")
    public OrsStatisticsResponse getOrsStatistics(@RequestParam String userId) {
        return departmentService.getOrsStatisticsByUserId(userId);
    }

    @GetMapping("/srs")
    public List<SrsResponse> getUserSessions(@RequestParam String userId) {
        return userService.getSrsByUserId(userId);
    }

    @GetMapping("/statistics/srs")
    public SrsStatisticsResponse getSrsStatistics() {
        return departmentService.getSrsStatistics();
    }

    @PostMapping("/department/improvements/{userId}")
    public String processAndReturnImprovements(@PathVariable String userId) {
        return departmentService.processImprovements(userId);
    }
}