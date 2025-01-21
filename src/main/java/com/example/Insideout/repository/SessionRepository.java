package com.example.Insideout.repository;

import com.example.Insideout.dto.SessionInfo;
import com.example.Insideout.entity.Session;
import com.example.Insideout.entity.Session.AgreementType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT new com.example.Insideout.dto.SessionInfo(s.sessionId, s.createdAt, s.isClosed) " +
            "FROM Session s WHERE s.userId = :userId")
    List<SessionInfo> findAllSessionsByUserId(@Param("userId") String userId);

    List<Session> findByUserIdAndAgreement(String userId, AgreementType agreement);
}