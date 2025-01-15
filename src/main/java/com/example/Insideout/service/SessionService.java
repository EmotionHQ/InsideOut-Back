package com.example.Insideout.service;

import com.example.Insideout.dto.SessionCreationRequest;
import com.example.Insideout.dto.SessionInfo;
import com.example.Insideout.dto.SessionResponse;
import com.example.Insideout.entity.Message;
import com.example.Insideout.entity.Message.AuthorType;
import com.example.Insideout.entity.Session;
import com.example.Insideout.repository.MessageRepository;
import com.example.Insideout.repository.SessionRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final MessageRepository messageRepository;

    public SessionService(SessionRepository sessionRepository, MessageRepository messageRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    /*
    세션 생성
     */
    @Transactional
    public SessionResponse createNewSession(SessionCreationRequest requestDTO) {
        // 새로운 세션 생성
        Session session = new Session();
        session.setUserId(requestDTO.getUserId());
        session = sessionRepository.save(session);

        // 초기 메시지 생성
        Message message = new Message();
        message.setSession(session);
        message.setContent("안녕! 내 이름은 마음이야. 무슨 고민이 있니?");
        message.setAuthorType(AuthorType.AI);
        messageRepository.save(message);

        return new SessionResponse(session.getSessionId(), session.getUserId(), session.getCreatedAt());
    }

    /*
    유저 아이디로 세션 정보 조회
     */
    public List<SessionInfo> getSessionsByUserId(String userId) {
//        if (sessions.isEmpty()) {
//            throw new IllegalArgumentException("해당 유저는 세션이 없습니다");
//        }

        return sessionRepository.findAllSessionsByUserId(userId);
    }
}
