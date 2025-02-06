package com.example.Insideout.repository;

import com.example.Insideout.dto.SessionInfo;
import com.example.Insideout.entity.Session;
import com.example.Insideout.entity.Session.AgreementType;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT new com.example.Insideout.dto.SessionInfo(s.sessionId, s.createdAt, s.isClosed) " +
            "FROM Session s WHERE s.userId = :userId ORDER BY s.createdAt ASC")
    List<SessionInfo> findAllSessionsByUserId(@Param("userId") String userId);

    List<Session> findByUserIdAndAgreement(String userId, AgreementType agreement);

    List<Session> findAllByUserId(String userId);

    List<Session> findAllByUserIdOrderByCreatedAtAsc(String userId);

    List<Session> findAllByUserIdIn(List<String> userIds);

    List<Session> findAllByIsClosedTrueOrderByCreatedAtAsc();

    Optional<Session> findBySessionId(Long sessionId);

    @Query("SELECT s.sessionId FROM Session s WHERE s.userId IN :userIds AND s.createdAt >= :startDate AND s.agreement = 'ACCEPTED'")
    List<Long> findAcceptedSessions(List<String> userIds, LocalDateTime startDate);

    void deleteAllByUserId(String userId);
}