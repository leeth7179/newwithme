package com.javalab.student.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javalab.student.dto.MessageRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
public class MessagePublisherService {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    // ✅ 사용자별 채널명을 생성할 때 사용
    private static final String CHANNEL_PREFIX = "chat_channel_";

    /**
     * ✅ 생성자 주입 시 @Qualifier 적용 (redisStringTemplate 사용)
     */
    public MessagePublisherService(
            @Qualifier("redisStringTemplate") RedisTemplate<String, String> redisTemplate,
            ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * ✅ 메시지를 Redis Pub/Sub으로 발행하는 메서드 (DB 저장 X)
     * - 수신자별로 채널 생성
     */
    public void publishMessage(Long receiverId, Long senderId, String messageContent) {
        log.info("📨 Redis 메시지 발행 요청 - receiverId={}, senderId={}, content={}", receiverId, senderId, messageContent);

        if (receiverId == null || senderId == null) {
            log.error("❌ 메시지 발행 실패: 수신자 ID 또는 발신자 ID가 누락되었습니다.");
            throw new IllegalArgumentException("수신자 ID 또는 발신자 ID가 누락되었습니다.");
        }

        try {
            // ✅ 수신자별 채널명 생성
            String channelName = CHANNEL_PREFIX + receiverId;

            // ✅ 메시지를 JSON 형식으로 변환
            String jsonMessage = objectMapper.writeValueAsString(
                    MessageRequestDto.builder()
                            .receiverId(receiverId)
                            .senderId(senderId)
                            .content(messageContent)
                            .build()
            );

            // ✅ Redis Pub/Sub으로 발행
            redisTemplate.convertAndSend(channelName, jsonMessage);

            log.info("📩 Redis 메시지 발행 완료! receiverId={}, senderId={}, content={}", receiverId, senderId, messageContent);

        } catch (Exception e) {
            log.error("❌ 메시지 발행 중 오류 발생", e);
            throw new RuntimeException("메시지 발행 실패", e);
        }
    }

    /**
     * ✅ 메시지 업데이트 정보를 Redis Pub/Sub으로 발행하는 메서드
     */
    public void publishMessageUpdate(Long messageId, String action) {
        log.info("📨 Redis 메시지 업데이트 발행 요청 - messageId={}, action={}", messageId, action);

        try {
            String channel = CHANNEL_PREFIX + messageId;
            String jsonMessage = objectMapper.writeValueAsString(
                    Map.of("messageId", messageId, "action", action)
            );
            redisTemplate.convertAndSend(channel, jsonMessage);
            log.info("📩 Redis 메시지 업데이트 발행 완료! messageId={}, action={}", messageId, action);
        } catch (Exception e) {
            log.error("❌ 메시지 업데이트 발행 중 오류 발생", e);
            throw new RuntimeException("메시지 업데이트 발행 실패", e);
        }
    }
}
