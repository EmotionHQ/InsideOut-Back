package com.example.Insideout.service;

import com.example.Insideout.dto.MessageRequest;
import com.example.Insideout.dto.MessageResponse;
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
    private final FastApiClient fastApiClient;

    public SessionService(SessionRepository sessionRepository, MessageRepository messageRepository,
                          FastApiClient fastApiClient) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
        this.fastApiClient = fastApiClient;
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
        return sessionRepository.findAllSessionsByUserId(userId);
    }

    /*
    세션 아이디로 메세지 정보 조회
     */
    public List<MessageResponse> getMessagesBySessionId(Long sessionId) {
        return messageRepository.findMessagesBySessionId(sessionId);
    }

    /*
    ORS 점수 업데이트
     */
    @Transactional
    public void updateOrsScore(Long sessionId, Integer orsScore) {
        Session session = getSession(sessionId);

        session.setOrsScore(orsScore);

        sessionRepository.save(session);
    }

    /*
    세션 종료 - srs, 동의 여부 업데이트
     */
    @Transactional
    public void endSession(Long sessionId, Integer srsScore, Session.AgreementType agreement) {
        Session session = getSession(sessionId);

        if (session.isClosed()) {
            throw new IllegalStateException("이미 종료된 세션입니다");
        }
        session.setSrsScore(srsScore);
        session.setAgreement(agreement);
        session.setClosed(true);

        sessionRepository.save(session);
    }

    private Session getSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("해당 세션이 존재하지 않습니다: " + sessionId));
    }

    /**
     * 메세지 전달 및 저장
     */
    public MessageResponse processMessage(MessageRequest messageRequest) {
        // sessionId로 세션 조회
        Session session = sessionRepository.findById(messageRequest.getSessionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 세션이 존재하지 않습니다: " + messageRequest.getSessionId()));

        // userMessage 생성 및 DB 저장
        Message userMessage = new Message();
        userMessage.setSession(session);
        userMessage.setContent(messageRequest.getContent());
        userMessage.setAuthorType(Message.AuthorType.USER);
        userMessage.setCreatedAt(messageRequest.getCreatedAt());
        messageRepository.save(userMessage);

        // FastAPI로 메시지 전달 및 응답 받기
        MessageResponse aiResponse = fastApiClient.sendMessageToFastApi(messageRequest.getSessionId());

        // AI 메시지 생성 및 저장
        Message aiMessage = new Message();
        aiMessage.setSession(session);
        aiMessage.setContent(aiResponse.getContent());
        aiMessage.setAuthorType(Message.AuthorType.AI);
        aiMessage.setCreatedAt(aiResponse.getCreatedAt());
        messageRepository.save(aiMessage);

        return aiResponse;
    }
}
