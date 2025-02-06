package com.example.Insideout.controller;

import com.example.Insideout.dto.DepartmentInfoResponse;
import com.example.Insideout.dto.OrsStatisticsResponse;
import com.example.Insideout.dto.SessionIdResponse;
import com.example.Insideout.dto.SrsResponse;
import com.example.Insideout.dto.SrsStatisticsResponse;
import com.example.Insideout.dto.UserInfoResponse;
import com.example.Insideout.service.DepartmentService;
import com.example.Insideout.service.JwtUtil;
import com.example.Insideout.service.SessionService;
import com.example.Insideout.service.UserService;
import io.jsonwebtoken.JwtException;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manage")
public class ManageController {

    private final UserService userService;
    private final SessionService sessionService;
    private final DepartmentService departmentService;
    private final JwtUtil jwtUtil;

    public ManageController(UserService userService, SessionService sessionService,
                            DepartmentService departmentService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.sessionService = sessionService;
        this.departmentService = departmentService;
        this.jwtUtil = jwtUtil;
    }

    @Operation(
            description = "매니저 아이디로 부서원 정보 반환 (부서 관리자)"
    )
    @GetMapping(value = "/department/users")
    public ResponseEntity<Page<UserInfoResponse>> getUsersInSameDepartment(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String memberName,
            @PageableDefault(size = 4, sort = "name") Pageable pageable
    ) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            return ResponseEntity.ok(departmentService.getUsersInSameDepartment(userId, memberName, pageable));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(
            description = "부서명으로 부서원 정보 반환 (웹 관리자)"
    )
    @GetMapping(value = "/department/users/by", params = "departmentName")
    public ResponseEntity<Page<UserInfoResponse>> getUsersByDepartmentName(
            @RequestHeader("Authorization") String token,
            @RequestParam String departmentName,
            @RequestParam(required = false) String memberName,
            @PageableDefault(size = 5, sort = "name") Pageable pageable
    ) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            return ResponseEntity.ok(
                    departmentService.getUsersByDepartmentName(userId, departmentName, memberName, pageable));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(
            description = "유저가 동의한 세션 리스트 반환"
    )
    @GetMapping("/accepted")
    public ResponseEntity<List<SessionIdResponse>> getAcceptedSessions(@RequestHeader("Authorization") String token,
                                                                       @RequestParam String userId) {
        try {
            String jwtUserId = jwtUtil.validateAndExtractUserId(token);
            return ResponseEntity.ok(sessionService.getAcceptedSessionsByUserId(jwtUserId, userId));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/departments")
    public ResponseEntity<Page<DepartmentInfoResponse>> getAllDepartmentsInfo(
            @RequestHeader("Authorization") String token,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 4, sort = "deptCode") Pageable pageable
    ) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            return ResponseEntity.ok(departmentService.getAllDepartmentInfo(userId, keyword, pageable));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(
            description = "부서의 ors 통계 (평균, 분산)"
    )
    @GetMapping("/statistics/ors")
    public ResponseEntity<OrsStatisticsResponse> getOrsStatistics(@RequestHeader("Authorization") String token) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            return ResponseEntity.ok(departmentService.getOrsStatisticsByUserId(userId));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/srs")
    public List<SrsResponse> getUserSessions(@RequestParam String userId) {
        return userService.getSrsByUserId(userId);
    }

    @Operation(
            description = "모든 유저의 srs 통계 (평균, 분산)"
    )
    @GetMapping("/statistics/srs")
    public ResponseEntity<SrsStatisticsResponse> getSrsStatistics(@RequestHeader("Authorization") String token) {
        try {
            String userId = jwtUtil.validateAndExtractUserId(token);
            return ResponseEntity.ok(departmentService.getSrsStatistics(userId));
        } catch (JwtException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/department/improvements/{userId}")
    public String processAndReturnImprovements(@PathVariable String userId) {
        return departmentService.processImprovements(userId);
    }
}