package com.javalab.student.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.javalab.student.entity.Member;

/**
 * Member 엔티티의 데이터베이스 작업을 처리하는 리포지토리
 */
@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 이메일로 회원 조회 (로그인 시 사용)
    //Optional<Member> findByEmail(String email);

    // 전화번호로 회원 조회 (중복 체크 시 사용)
    //Optional<Member> findByPhone(String phone);

    Member findByEmail(String email);
    Member findByPhone(String phone);

    boolean existsByEmail(String email);

    @Query("SELECT m FROM Member m WHERE m.regTime >= :threeMonthsAgo")
    List<Member> findRecentMembers(@Param("threeMonthsAgo") LocalDateTime threeMonthsAgo);


}
