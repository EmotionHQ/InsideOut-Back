package com.example.Insideout.service;

import com.example.Insideout.dto.DepartmentInfoResponse;
import com.example.Insideout.dto.OrsStatisticsResponse;
import com.example.Insideout.dto.OrsStatisticsResponse.OrsStats;
import com.example.Insideout.dto.UserDto;
import com.example.Insideout.dto.UserInfoResponse;
import com.example.Insideout.entity.Department;
import com.example.Insideout.entity.Session;
import com.example.Insideout.entity.User;
import com.example.Insideout.entity.User.Role;
import com.example.Insideout.repository.DepartmentRepository;
import com.example.Insideout.repository.SessionRepository;
import com.example.Insideout.repository.UserRepository;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;

    public DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository,
                             SessionRepository sessionRepository) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    /**
     * 고유한 부서 코드 생성(중복 체크 포함)
     */
    public String generateUniqueDeptCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (departmentRepository.existsByDeptCode(code)); // 중복 체크
        return code;
    }

    /**
     * 랜덤 부서 코드 생성
     */
    private String generateRandomCode() {
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int n = random.nextInt(36);
            if (n < 10) {
                codeBuilder.append(n);
            } else {
                codeBuilder.append((char) (n + 55));
            }
        }
        return codeBuilder.toString();
    }

    /**
     * 부서 코드를 기반으로 부서명 조회
     */

    public String findDepartmentByDeptCode(String deptCode) {
        return departmentRepository.findByDeptCode(deptCode)
                .map(Department -> Department.getDepartment())
                .orElseThrow(() -> new IllegalArgumentException("Invalid department code"));
    }

    /**
     * 부서장 회원 가입 시 부서 저장 (부서 코드, 부서명)
     */
    public void saveDepartmentFromUserDto(UserDto userDto) {
        Department department = new Department();

        if (departmentRepository.existsByDepartment(userDto.getDepartment())) {
            throw new IllegalArgumentException("해당 부서가 이미 존재합니다: " + userDto.getDepartment());
        }
        department.setDeptCode(userDto.getDeptCode());
        department.setDepartment(userDto.getDepartment());

        departmentRepository.save(department);
    }

    /**
     * 부서 정보 + 부서 매니저 이름 반환
     */
    public List<DepartmentInfoResponse> getAllDepartmentInfo() {
        return departmentRepository.findAllDepartmentsWithManagers();
    }

    /**
     * 부서에 속한 부서원 정보 반환
     */
    public List<UserInfoResponse> getUsersInSameDepartment(String userId) {

        User manager = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        if (manager.getRole() != Role.MANAGER) {
            throw new IllegalArgumentException("해당 유저는 매니저가 아닙니다.");
        }

        List<User> usersInDepartment = userRepository.findAllByDeptCode(manager.getDeptCode())
                .stream()
                .filter(user -> user.getRole() == Role.USER)
                .toList();

        return usersInDepartment.stream()
                .map(u -> new UserInfoResponse(
                        u.getName(),  // 이름 반환
                        u.getUserId() // 아이디 반환
                ))
                .collect(Collectors.toList());
    }

    /**
     * 부서에 속한 모든 유저 정보 반환
     */
    public List<UserInfoResponse> getUsersByDepartmentName(String departmentName) {
        Department department = departmentRepository.findByDepartment(departmentName)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다: " + departmentName));

        List<User> users = userRepository.findAllByDeptCode(department.getDeptCode());

        return users.stream()
                .map(user -> new UserInfoResponse(
                        user.getName(),
                        user.getUserId(),
                        user.getEmail(),
                        user.getPhoneNumber(),
                        user.getRole()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 부서의 ORS 평균,분산 (주간 단위) 반환
     */
    public OrsStatisticsResponse getOrsStatisticsByUserId(String userId) {
        // 유저 정보 조회 및 부서 코드 가져오기
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        String deptCode = user.getDeptCode();

        // 동일한 부서 코드를 가진 유저들의 ID 조회
        List<User> usersInDepartment = userRepository.findAllByDeptCode(deptCode);
        List<String> userIds = usersInDepartment.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        // 해당 유저들의 세션 가져오기
        List<Session> sessions = sessionRepository.findAllByUserIdIn(userIds);

        // 주 단위로 그룹화하여 평균 및 분산 계산 (null 값 제외)
        Map<LocalDate, List<Integer>> weeklyOrsScores = sessions.stream()
                .filter(session -> session.getOrsScore() != null)
                .collect(Collectors.groupingBy(
                        session -> session.getCreatedAt().toLocalDate()
                                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1),
                        Collectors.mapping(Session::getOrsScore, Collectors.toList())
                ));

        Map<LocalDate, OrsStats> weeklyStats = new HashMap<>();

        for (Map.Entry<LocalDate, List<Integer>> entry : weeklyOrsScores.entrySet()) {
            double average = entry.getValue().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            double variance = entry.getValue().stream()
                    .mapToDouble(score -> Math.pow(score - average, 2))
                    .average()
                    .orElse(0.0);

            weeklyStats.put(entry.getKey(), new OrsStats(average, variance));
        }

        return new OrsStatisticsResponse(weeklyStats);
    }

//    public Department saveDepartment(DepartmentDto departmentDto) {
//        Department department = new Department();
//
//        department.setDeptCode(departmentDto.getDeptCode());
//        if (departmentRepository.existsByDepartment(departmentDto.getDepartment())) {
//            throw new IllegalArgumentException("해당 부서가 이미 존재합니다: " + departmentDto.getDepartment());
//        }
//        department.setDepartment(departmentDto.getDepartment());
//
//        return departmentRepository.save(department);
//    }
}

