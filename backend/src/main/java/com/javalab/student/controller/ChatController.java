package com.javalab.student.controller;

import com.javalab.student.dto.MessageRequestDto;
import com.javalab.student.dto.MessageResponseDto;
import com.javalab.student.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

/**
 * WebSocket 기반 실시간 메시지 전송[미사용]
 * - 클라이언트가 "/app/chat"으로 메시지를 보내면 이 메서드가 호출되어 메시지를 처리하고
 *  "/topic/chat"으로 구독한 클라이언트들에게 메시지를 전송합니다.
 * - 이 컨트롤러는 웹소켓만을 써서 메시지를 송수신하는 경우에 사용되고 현재와 같이
 *   Rest Api 형태로 메시지를 전송하고 중간에 Redis를 사용하여 메시지를 중계하고 또 그 메시지를
 *   Redis Subscriber에서 받아서 WebSocket을 통해 클라이언트에게 전달하는 경우에는 사용하지 않음.
 */
@Controller
@Slf4j
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService; // 메시지 서비스 주입 필요

    /**
     * 특정 사용자에게 메시지 전송
     * @param messageRequestDto 메시지 요청 DTO
     */
    @MessageMapping("/chat/{userId}")
    public void sendMessageToUser(@DestinationVariable String userId, MessageRequestDto messageRequestDto) {
        try {
            // userId를 receiverId로 설정
            messageRequestDto.setReceiverId(Long.parseLong(userId));

            // 기존 saveMessage 메서드 사용
            MessageResponseDto responseDto = messageService.saveMessage(messageRequestDto);

            log.info("📨 메시지 수신 - UserId: {}, 메시지: {}", userId, messageRequestDto);
            log.info("📤 메시지 전송 성공 - 메시지ID: {}", responseDto.getId());
        } catch (Exception e) {
            log.error("❌ 메시지 전송 중 오류 발생", e);
        }
    }

    /**
     * 개인 메시지 전송 (1:1 채팅)
     */
    @MessageMapping("/private")
    public void sendPrivateMessage(String message, String userId) {
        messagingTemplate.convertAndSendToUser(userId, "/queue/private", message);
    }
}