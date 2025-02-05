package com.example.Insideout.service;

import com.example.Insideout.dto.MessageRequest;
import com.example.Insideout.dto.MessageResponse;
import com.example.Insideout.dto.SessionIdResponse;
import com.example.Insideout.dto.SessionInfo;
import com.example.Insideout.dto.SessionResponse;
import com.example.Insideout.dto.SessionSummaryResponse;
import com.example.Insideout.entity.Message;
import com.example.Insideout.entity.Message.AuthorType;
import com.example.Insideout.entity.Session;
import com.example.Insideout.entity.Session.AgreementType;
import com.example.Insideout.repository.MessageRepository;
import com.example.Insideout.repository.SessionRepository;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
    public SessionResponse createNewSession(String userId) {
        // 새로운 세션 생성
        Session session = new Session();
        session.setUserId(userId);
        session = sessionRepository.save(session);

        // 초기 메시지 생성
        Message message = new Message();
        message.setSession(session);
        message.setContent("마음이 무거운가요? 여기는 감정본부입니다. 작은 고민도 괜찮아요. 함께 이야기하면서 정리해 봐요.");
        message.setAuthorType(AuthorType.AI);
        messageRepository.save(message);

        return new SessionResponse(session.getSessionId(), session.getUserId(), session.getCreatedAt());
    }

    /**
     * 세션 삭제
     */
    @Transactional
    public void deleteSession(Long sessionId) {
        Session session = getSession(sessionId);

        // 관련 메시지 먼저 삭제
        messageRepository.deleteBySession(session);

        // 세션 삭제
        sessionRepository.delete(session);
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
        userMessage.setImageUrl(messageRequest.getImageUrl());
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

    /**
     * user의 동의 여부 ACCEPTED 세션들 반환
     */
    public List<SessionIdResponse> getAcceptedSessionsByUserId(String userId) {
        List<Session> acceptedSessions = sessionRepository.findByUserIdAndAgreement(userId, AgreementType.ACCEPTED);

        return acceptedSessions.stream()
                .sorted(Comparator.comparing(Session::getCreatedAt))  // createdAt 기준 오름차순 정렬
                .map(session -> new SessionIdResponse(session.getSessionId(), session.getCreatedAt()))
                .collect(Collectors.toList());
    }

    /**
     * 세션의 요약, 개선 사항, 상태 저장
     */
    @Transactional
    public void SummarizeAndUpdateSession(Long sessionId) {
        SessionSummaryResponse response = fastApiClient.getSessionSummary(sessionId);

        Session session = sessionRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("세션을 찾을 수 없습니다: " + sessionId));

        session.setSummary(response.getSummary());
        session.setStatus(response.getStatus()); // RISK or STABLE
        session.setFeedback(response.getFeedback());

        sessionRepository.save(session);
    }

    /**
     * 세션의 요약, 개선 사항, 상태 반환
     */
    public List<SessionSummaryResponse> getSessionDetails(String userId) {
        List<Session> mySessions = sessionRepository.findAllByUserIdOrderByCreatedAtAsc(userId);

        return mySessions.stream()
                .map(session -> new SessionSummaryResponse(
                        session.getOrsScore(),
                        session.getSummary(),
                        session.getStatus(),
                        session.getFeedback(),
                        session.getCreatedAt()
                ))
                .collect(Collectors.toList());
    }
}
