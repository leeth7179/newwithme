package com.javalab.student.repository;

import com.javalab.student.entity.Member;
import com.javalab.student.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // ✅ 받은 메시지를 최신순으로 조회
    List<Message> findByReceiverOrderByRegTimeDesc(Member receiver);

    // ✅ 보낸 메시지를 최신순으로 조회
    List<Message> findBySenderOrderByRegTimeDesc(Member sender);

    // ✅ 로그인 시 읽지 않은 메시지 개수를 조회하여 배지(알람) 표시
    // 아래처럼 @Query를 명시적으로 사용하면 @Param("receiver") 이 필요함
    @Query("SELECT COUNT(m) FROM Message m WHERE m.receiver = :receiver AND m.read = false")
    int countUnreadMessages(@Param("receiver") Member receiver);

    @Modifying(clearAutomatically = true)  // ✅ 엔티티 컨텍스트 자동 동기화
    @Transactional
    @Query("UPDATE Message m SET m.read = true WHERE m.id = :messageId")
    void markMessageAsRead(@Param("messageId") Long messageId);

    // ✅ 사용자의 받은 모든 메시지 조회 (페이지네이션 적용)
    Page<Message> findByReceiverOrderByRegTimeDesc(Member receiver, Pageable pageable);

    // ✅ 특정 사용자와의 대화 메시지 조회
    @Query("SELECT m FROM Message m WHERE (m.sender = :user AND m.receiver = :targetUser) OR (m.sender = :targetUser AND m.receiver = :user) ORDER BY m.regTime ASC")
    List<Message> findConversation(@Param("user") Member user, @Param("targetUser") Member targetUser);

    // ✅ 메시지 편집
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.content = :newContent, m.edited = true WHERE m.id = :messageId")
    void editMessage(@Param("messageId") Long messageId, @Param("newContent") String newContent);

    // ✅ 메시지 삭제 (발신자)
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.deletedBySender = true WHERE m.id = :messageId AND m.sender = :user")
    void deleteMessageBySender(@Param("messageId") Long messageId, @Param("user") Member user);

    // ✅ 메시지 삭제 (수신자)
    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.deletedByReceiver = true WHERE m.id = :messageId AND m.receiver = :user")
    void deleteMessageByReceiver(@Param("messageId") Long messageId, @Param("user") Member user);
}
