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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    public Page<DepartmentInfoResponse> getAllDepartmentInfo(String userId, String keyword, Pageable pageable) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("해당 유저는 관리자가 아닙니다.");
        }

        if (keyword == null || keyword.isEmpty()) {
            return departmentRepository.findAllDepartmentsWithManagers(pageable);
        }

        return departmentRepository.findAllDepartmentsWithManagersByKeyword(keyword, pageable);
    }

    /**
     * 부서에 속한 부서원 정보 반환
     */
    public Page<UserInfoResponse> getUsersInSameDepartment(String userId, String memberName, Pageable pageable) {

        User manager = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        if (manager.getRole() != Role.MANAGER) {
            throw new IllegalArgumentException("해당 유저는 매니저가 아닙니다.");
        }

        String searchName = (memberName != null && !memberName.isEmpty()) ? memberName : "";

        Page<User> usersPage = userRepository.findAllByDeptCodeAndRoleAndNameContaining(
                manager.getDeptCode(), Role.USER, searchName, pageable);

        return usersPage.map(user -> new UserInfoResponse(user.getName(), user.getUserId()));
    }

    /**
     * 부서에 속한 모든 유저 정보 반환
     */
    public Page<UserInfoResponse> getUsersByDepartmentName(String userId, String departmentName, String memberName,
                                                           Pageable pageable) {

        User manager = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        if (manager.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("해당 유저는 관리자가 아닙니다.");
        }

        Department department = departmentRepository.findByDepartment(departmentName)
                .orElseThrow(() -> new IllegalArgumentException("해당 부서를 찾을 수 없습니다: " + departmentName));

        String searchName = (memberName != null && !memberName.isEmpty()) ? memberName : "";

        Page<User> userPage = userRepository.findAllByDeptCodeAndNameContaining(department.getDeptCode(), searchName,
                pageable);

        return userPage.map(user -> new UserInfoResponse(
                user.getName(),
                user.getUserId(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole()
        ));
    }

    /**
     * 부서의 ORS 평균,분산 (주간 단위) 반환
     */
    public OrsStatisticsResponse getOrsStatisticsByUserId(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        if (user.getRole() == Role.USER) {
            throw new IllegalArgumentException("해당 유저는 관리자가 아닙니다.");
        }

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
    public SrsStatisticsResponse getSrsStatistics(String userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));

        if (user.getRole() != Role.ADMIN) {
            throw new IllegalArgumentException("해당 유저는 관리자가 아닙니다.");
        }
        List<Session> closedSessions = sessionRepository.findAllByIsClosedTrueOrderByCreatedAtAsc();

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

        // 정렬을 위한 TreeMap 사용
        Map<LocalDate, T> weeklyStats = new TreeMap<>();

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
    public String processImprovements(String userId) {

        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저를 찾을 수 없습니다: " + userId));
        if (user.getRole() != Role.MANAGER) {
            throw new IllegalArgumentException("해당 유저는 부서장이 아닙니다.");
        }

        String deptCode = userRepository.findByUserId(userId)
                .map(User::getDeptCode)
                .orElseThrow(() -> new IllegalArgumentException("유저가 존재하지 않습니다"));

        // 동일 부서의 모든 유저 ID 조회
        List<String> userIds = userRepository.findAllByDeptCode(deptCode)
                .stream().map(User::getUserId).toList();

        // 최근 30일 동안 생성된 세션 조회
        List<Long> sessionIds = sessionRepository.findSessions(userIds,
                LocalDateTime.now(ZoneId.of("Asia/Seoul")).minusDays(30));

        if (sessionIds.isEmpty()) {
            return "부서의 상담 세션이 존재하지 않습니다";
        }

        // FastAPI 호출하여 개선사항 가져오기
        String improvements = fastApiClient.getImprovements(sessionIds);

        if (improvements != null && !improvements.trim().isEmpty()) {
            Department department = departmentRepository.findByDeptCode(deptCode)
                    .orElseThrow(() -> new IllegalArgumentException("해당 부서가 존재하지 않습니다."));

            department.setImprovements(improvements);
            departmentRepository.save(department);
        }

        return parseToJSON(improvements);
    }

    /**
     * 전달받은 부서 개선사항 json 형식으로 파싱
     */
    public String parseToJSON(String input) {

        Pattern pattern = Pattern.compile("\\[(.*?)\\]\\s*\\n(.*?)(?=(\\n\\[|$))", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);

        Map<String, List<String>> categories = new HashMap<>();

        while (matcher.find()) {
            String category = matcher.group(1).trim();
            String content = matcher.group(2).trim();

            content = content.replace("-", "").trim();
            String[] items = content.split("\\n");

            List<String> cleanedItems = Arrays.stream(items)
                    .map(String::trim)
                    .filter(item -> !item.isEmpty())
                    .collect(Collectors.toList());

            categories.put(category, cleanedItems);
        }

        // JSON 객체로 변환
        JSONObject jsonObject = new JSONObject();
        for (Map.Entry<String, List<String>> entry : categories.entrySet()) {
            jsonObject.put(entry.getKey(), new JSONArray(entry.getValue()));
        }

        return jsonObject.toString(4);
    }

}

