package com.javalab.student.service;

import com.javalab.student.dto.MessageRequestDto;
import com.javalab.student.dto.MessageResponseDto;
import com.javalab.student.entity.Member;
import com.javalab.student.entity.Message;
import com.javalab.student.repository.MemberRepository;
import com.javalab.student.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;
    private final MemberRepository memberRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final MessagePublisherService messagePublisherService;

    /**
     * 메시지를 DB에 저장하고 WebSocket으로 발행
     */
    @Transactional
    public MessageResponseDto saveMessage(MessageRequestDto requestDto) {
        try {
            // 1. 필수 파라미터 검증
            if (requestDto.getSenderId() == null || requestDto.getReceiverId() == null) {
                throw new IllegalArgumentException("발신자 또는 수신자 ID가 누락되었습니다.");
            }

            // 2. 발신자와 수신자 조회
            Member sender = memberRepository.findById(requestDto.getSenderId())
                    .orElseThrow(() -> new IllegalArgumentException("발신자를 찾을 수 없습니다."));
            Member receiver = memberRepository.findById(requestDto.getReceiverId())
                    .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));

            // 3. 메시지 내용 검증
            if (requestDto.getContent() == null || requestDto.getContent().trim().isEmpty()) {
                throw new IllegalArgumentException("메시지 내용이 비어있습니다.");
            }

            // 4. 메시지 엔티티 생성
            Message savedMessage = Message.builder()
                    .sender(sender)
                    .receiver(receiver)
                    .content(requestDto.getContent().trim())
                    .read(false)
                    .build();

            // 5. 메시지 저장
            savedMessage = messageRepository.save(savedMessage);

            // 6. DTO 변환
            MessageResponseDto responseDto = new MessageResponseDto(savedMessage);

            // 7. WebSocket으로 메시지 전송
            String destination = "/topic/chat/" + receiver.getId();
            messagingTemplate.convertAndSend(destination, responseDto);
            log.info("📡 WebSocket 메시지 전송: {} → {}", savedMessage.getContent(), destination);

            return responseDto;

        } catch (IllegalArgumentException e) {
            log.error("❌ 메시지 저장 실패: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("❌ 예상치 못한 오류 발생", e);
            throw new RuntimeException("메시지 저장 중 오류 발생", e);
        }
    }

    /**
     * 메시지를 읽음 처리
     */
    @Transactional
    public void markMessageAsRead(Long messageId) {
        try {
            Message message = messageRepository.findById(messageId)
                    .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

            if (!message.isRead()) {
                message.setRead(true);
                messageRepository.save(message);
                messagePublisherService.publishMessageUpdate(messageId, "READ");
                log.info("✅ 메시지 읽음 처리: {}", messageId);
            }
        } catch (Exception e) {
            log.error("❌ 메시지 읽음 처리 중 오류", e);
            throw new RuntimeException("메시지 읽음 처리 실패", e);
        }
    }

    /**
     * ✅ 메시지를 DB에 저장하고 WebSocket으로 발행
     */
    @Transactional
    public MessageResponseDto saveMessageAndConvert(MessageRequestDto requestDto) {
        // 기존 saveMessage 메서드의 로직을 약간 수정
        Member sender = memberRepository.findById(requestDto.getSenderId())
                .orElseThrow(() -> new IllegalArgumentException("발신자를 찾을 수 없습니다."));
        Member receiver = memberRepository.findById(requestDto.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("수신자를 찾을 수 없습니다."));

        Message savedMessage = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(requestDto.getContent())
                .read(false)
                .build();

        savedMessage = messageRepository.save(savedMessage);

        // DTO로 변환
        MessageResponseDto responseDto = new MessageResponseDto(savedMessage);

        // WebSocket 전송
        String destination = "/topic/chat/" + receiver.getId();
        messagingTemplate.convertAndSend(destination, responseDto);
        log.info("📡 WebSocket 메시지 전송: {} → {}", savedMessage.getContent(), destination);

        return responseDto;
    }

    /**
     * ✅ 사용자 ID로 메시지 조회 (페이지네이션 적용)
     */
    public Page<MessageResponseDto> getMessagesByUserId(Long userId, int page, int size) {
        Member recipient = memberRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("수신자 정보를 찾을 수 없습니다."));
        Pageable pageable = PageRequest.of(page, size, Sort.by("regTime").descending());
        Page<Message> messages = messageRepository.findByReceiverOrderByRegTimeDesc(recipient, pageable);
        return messages.map(MessageResponseDto::new);
    }

    /**
     * ✅ 메시지 편집
     */
    @Transactional
    public MessageResponseDto editMessage(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));

        // 필요한 경우 편집 로직 추가
        message.setEdited(true);

        Message savedMessage = messageRepository.save(message);
        messagePublisherService.publishMessageUpdate(messageId, "EDIT");

        return new MessageResponseDto(savedMessage);
    }

    /**
     * ✅ 메시지 삭제
     */
    @Transactional
    public void deleteMessage(Long messageId, Long userId, boolean isSender) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new IllegalArgumentException("메시지를 찾을 수 없습니다."));
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (isSender) {
            message.setDeletedBySender(true);
        } else {
            message.setDeletedByReceiver(true);
        }
        messageRepository.save(message);
        messagePublisherService.publishMessageUpdate(messageId, "DELETE");
    }

    /**
     * ✅ 특정 사용자와의 대화 메시지 조회
     */
    public List<MessageResponseDto> getConversation(Long userId, Long targetUserId) {
        Member user = memberRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Member targetUser = memberRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("대화 상대를 찾을 수 없습니다."));

        List<Message> messages = messageRepository.findConversation(user, targetUser);
        return messages.stream()
                .map(MessageResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 사용자가 보낸 메시지 조회
     */
    public List<MessageResponseDto> getSentMessages(Long userId) {
        try {
            Member sender = memberRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            return messageRepository.findBySenderOrderByRegTimeDesc(sender)
                    .stream()
                    .map(MessageResponseDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("보낸 메시지 조회 중 오류 발생", e);
            throw new RuntimeException("보낸 메시지 조회 실패", e);
        }
    }

    /**
     * 사용자가 받은 메시지 조회
     */
    public List<MessageResponseDto> getReceivedMessages(Long userId) {
        try {
            Member receiver = memberRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            return messageRepository.findByReceiverOrderByRegTimeDesc(receiver)
                    .stream()
                    .map(MessageResponseDto::new)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("받은 메시지 조회 중 오류 발생", e);
            throw new RuntimeException("받은 메시지 조회 실패", e);
        }
    }

    /**
     * 사용자의 읽지 않은 메시지 개수 조회
     */
    public int getUnreadMessageCount(Long userId) {
        try {
            Member receiver = memberRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

            return messageRepository.countUnreadMessages(receiver);
        } catch (Exception e) {
            log.error("읽지 않은 메시지 개수 조회 중 오류 발생", e);
            throw new RuntimeException("읽지 않은 메시지 개수 조회 실패", e);
        }
    }
}
