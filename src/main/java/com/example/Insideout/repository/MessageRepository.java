package com.example.Insideout.repository;

import com.example.Insideout.dto.MessageResponse;
import com.example.Insideout.entity.Message;
import com.example.Insideout.entity.Session;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    @Query("SELECT new com.example.Insideout.dto.MessageResponse(m.content, m.authorType, m.createdAt, m.imageUrl) " +
            "FROM Message m WHERE m.session.sessionId = :sessionId")
    List<MessageResponse> findMessagesBySessionId(@Param("sessionId") Long sessionId);

    void deleteBySession(Session session);

    void deleteAllBySessionIn(List<Session> sessions);
}