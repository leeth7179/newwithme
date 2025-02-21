package com.javalab.student.service;

import com.javalab.student.entity.Member;
import com.javalab.student.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Redis 서비스 클래스 (권한 캐싱 및 메시지 전송 겸용)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final RedisTemplate<String, Object> redisObjectTemplate;
    private final MemberRepository memberRepository;

    /**
     * ✅ 사용자의 권한 정보를 Redis에 캐싱
     */
    public void cacheUserAuthorities(String email) {
        log.info("사용자 [{}]의 권한 정보를 Redis에 캐싱합니다.", email);

        // 1. 데이터베이스에서 사용자 조회
        Member member = memberRepository.findByEmail(email);
        if (member == null) {
            throw new IllegalArgumentException("해당 이메일을 가진 사용자가 존재하지 않습니다.");
        }

        // 2. 권한 리스트 추출
        List<String> authorities = member.getAuthorities().stream()
                .map(role -> role.getAuthority())
                .collect(Collectors.toList());

        // 3. Redis에 저장 (유효시간: 6시간)
        redisObjectTemplate.opsForValue().set("AUTH:" + email, authorities, Duration.ofHours(6));

        log.info("✅ 사용자 [{}]의 권한 정보가 Redis에 저장되었습니다: {}", email, authorities);
    }

    /**
     * ✅ Redis에서 사용자의 권한 정보 조회
     */
    public List<String> getUserAuthoritiesFromCache(String email) {
        Object data = redisObjectTemplate.opsForValue().get("AUTH:" + email);
        if (data instanceof List<?>) {
            return ((List<?>) data).stream()
                    .map(String.class::cast)
                    .collect(Collectors.toList());
        }
        log.warn("⚠️ Redis에서 [{}]의 권한 정보를 불러올 수 없습니다.", email);
        return List.of();
    }

    /**
     * ✅ Redis에서 사용자 권한 정보 삭제
     */
    public void removeUserAuthorities(String email) {
        redisObjectTemplate.delete("AUTH:" + email);
        log.info("🗑️ 사용자 [{}]의 권한 정보가 Redis에서 삭제되었습니다.", email);
    }

    /**
     * 🚀 **메시지 전송 (Redis Pub/Sub) 기능 개선**
     * - WebSocket 구독 채널을 사용자 ID 기반으로 동적으로 설정
     * @param receiverId 메시지 수신자의 ID
     * @param senderId 메시지 발신자의 ID
     * @param message 전송할 메시지 내용
     */
    public void publishChatMessage(Long receiverId, Long senderId, String message) {
        // 수신자 채널
        String receiverTopic = "/topic/chat/" + receiverId;
        // 발신자 채널
        String senderTopic = "/topic/chat/" + senderId;

        try {
            // 수신자에게 메시지 전송
            log.info("📢 Redis Pub/Sub 메시지 발행(수신자): {} → {}", message, receiverTopic);
            redisObjectTemplate.convertAndSend(receiverTopic, message);
            log.info("✅ 메시지가 Redis를 통해 발행되었습니다! → {}", receiverTopic);

            // 발신자에게 메시지 전송
            log.info("📢 Redis Pub/Sub 메시지 발행(발신자): {} → {}", message, senderTopic);
            redisObjectTemplate.convertAndSend(senderTopic, message);
            log.info("✅ 메시지가 Redis를 통해 발행되었습니다! → {}", senderTopic);

        } catch (Exception e) {
            log.error("🚨 Redis 메시지 발행 실패: {}", e.getMessage(), e);
            throw new RuntimeException("메시지 발행 실패!", e);
        }
    }
}
