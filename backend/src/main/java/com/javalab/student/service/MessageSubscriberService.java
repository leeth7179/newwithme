package com.javalab.student.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message; // ✅ Redis 메시지
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.javalab.student.dto.MessageRequestDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageSubscriberService implements org.springframework.data.redis.connection.MessageListener {

    private final SimpMessagingTemplate messagingTemplate; // ✅ WebSocket을 통해 클라이언트에게 메시지를 전송하는 역할
    private final ObjectMapper objectMapper;

    /**
     *  Redis 메시지 수신
     *  - Redis에서 메시지를 수신하는 역할.
     *  - redisMessage : Redis Publiser가 발행한 메시지
     *  - 이 역할을 수행한 후, 구독 중인 WebSocket 클라이언트에게 메시지를 전송하면 된다.
     *    메시지를 수신하고 WebSocket을 통해 클라이언트에게 보냅니다.
     */
    @Override
    public void onMessage(Message redisMessage, byte[] pattern) { // ✅ RedisMessage는 변수로 사용
        try {
            // 1. Redis 메시지 수신
            String jsonMessage = new String(redisMessage.getBody()); // Redis Publiser가 발행한 메시지 getBody()로 가져와서 String으로 변환
            log.info("🔹 Redis Subscriber 에서 수신한 경로 : {}, 메시지 내용: {}", new String(pattern), jsonMessage);

            // 2. 전달받은 메시지 내용을 MessageRequestDto로 변환
            MessageRequestDto messageDto = objectMapper.readValue(jsonMessage, MessageRequestDto.class);
            log.info("✅ WebSocket으로 메시지 전송: /topic/chat/{}", messageDto.getReceiverId());

            // 3. Redis에서 보낸 메시지를 전달받고 이를 WebSocket을 통해 클라이언트에게 전달
            messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getReceiverId(), objectMapper.writeValueAsString(messageDto));

            // 4. 발신자에게도 동일한 메시지 전송
            messagingTemplate.convertAndSend("/topic/chat/" + messageDto.getSenderId(), objectMapper.writeValueAsString(messageDto));

        } catch (Exception e) {
            log.error("❌ 메시지 처리 중 오류 발생", e);
        }
    }
}
