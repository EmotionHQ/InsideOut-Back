package com.example.Insideout.service;

import com.example.Insideout.dto.DepartmentInfoResponse;
import com.example.Insideout.dto.OrsStatisticsResponse;
import com.example.Insideout.dto.OrsStatisticsResponse.OrsStats;
import com.example.Insideout.dto.SrsStatisticsResponse;
import com.example.Insideout.dto.SrsStatisticsResponse.SrsStats;
import com.example.Insideout.dto.UserDto;
import com.example.Insideout.dto.UserInfoResponse;
import com.example.Insideout.entity.Department;
import com.example.Insideout.entity.Session;
import com.example.Insideout.entity.User;
import com.example.Insideout.entity.User.Role;
import com.example.Insideout.repository.DepartmentRepository;
import com.example.Insideout.repository.SessionRepository;
import com.example.Insideout.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final FastApiClient fastApiClient;

    public DepartmentService(DepartmentRepository departmentRepository, UserRepository userRepository,
                             SessionRepository sessionRepository, FastApiClient fastApiClient) {
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
        this.fastApiClient = fastApiClient;
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
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        String deptCode = user.getDeptCode();

        List<User> usersInDepartment = userRepository.findAllByDeptCode(deptCode);
        List<String> userIds = usersInDepartment.stream()
                .map(User::getUserId)
                .collect(Collectors.toList());

        List<Session> sessions = sessionRepository.findAllByUserIdIn(userIds);

        Map<LocalDate, OrsStats> weeklyStats = calculateWeeklyStats(sessions, Session::getOrsScore);

        return new OrsStatisticsResponse(weeklyStats);
    }

    /**
     * 전체 유저의 SRS 평균, 분산 (주간 단위) 반환
     */
    public SrsStatisticsResponse getSrsStatistics() {
        List<Session> closedSessions = sessionRepository.findAllByIsClosedTrue();

        Map<LocalDate, SrsStats> weeklyStats = calculateWeeklyStats(closedSessions, Session::getSrsScore);

        return new SrsStatisticsResponse(weeklyStats);
    }

    /**
     * 주 단위로 그룹화하여 평균 및 분산을 계산
     */
    private <T> Map<LocalDate, T> calculateWeeklyStats(
            List<Session> sessions,
            Function<Session, Integer> scoreExtractor
    ) {
        Map<LocalDate, List<Integer>> weeklyScores = sessions.stream()
                .filter(session -> scoreExtractor.apply(session) != null)
                .collect(Collectors.groupingBy(
                        session -> session.getCreatedAt().toLocalDate()
                                .with(WeekFields.of(Locale.getDefault()).dayOfWeek(), 1),
                        Collectors.mapping(scoreExtractor, Collectors.toList())
                ));

        Map<LocalDate, T> weeklyStats = new HashMap<>();

        for (Map.Entry<LocalDate, List<Integer>> entry : weeklyScores.entrySet()) {
            double average = entry.getValue().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);

            double variance = entry.getValue().stream()
                    .mapToDouble(score -> Math.pow(score - average, 2))
                    .average()
                    .orElse(0.0);

            weeklyStats.put(entry.getKey(), (T) new OrsStats(average, variance));
        }

        return weeklyStats;
    }

    /**
     * 부서 개선 사항 저장 및 반환
     */
    @Transactional
    public String processImprovements(String userId) {

        String deptCode = userRepository.findByUserId(userId)
                .map(User::getDeptCode)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다"));

        List<String> userIds = userRepository.findAllByDeptCode(deptCode)
                .stream().map(User::getUserId).toList();

        // 최근 30일 동안 생성된, AGREEMENT = ACCEPTED인 세션 조회
        List<Long> sessionIds = sessionRepository.findAcceptedSessions(userIds, LocalDateTime.now().minusDays(30));

        if (sessionIds.isEmpty()) {
            return "부서의 상담 세션이 존재하지 않습니다";
        }

        // FastAPI 호출하여 개선사항 가져오기
        String improvements = fastApiClient.getImprovements(sessionIds);

        // 비동기 DB 저장
        saveImprovementsAsync(deptCode, improvements);

        return improvements;
    }

    @Async
    @Transactional
    public CompletableFuture<Void> saveImprovementsAsync(String deptCode, String improvements) {

        if (improvements == null || improvements.trim().isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }

        Department department = departmentRepository.findByDeptCode(deptCode)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서가 존재하지 않습니다."));

        department.setImprovements(improvements);
        departmentRepository.save(department);

        return CompletableFuture.completedFuture(null);
    }
}

