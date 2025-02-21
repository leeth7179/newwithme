package com.javalab.student.config.jwt;

import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.javalab.student.config.jwt.JwtProperties;
import com.javalab.student.config.jwt.TokenProvider;

import java.time.Duration;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class TokenProviderTest {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    @DisplayName("getUserId(): 토큰으로 Member ID를 가져올 수 있다.")
    void getUserId() {
        // Given: 회원 ID를 포함한 JWT 토큰 생성
        Long memberId = 1L; // 테스트용 회원 ID
        String token = Jwts.builder()
                .setClaims(Map.of("id", memberId)) // 클레임에 회원 ID 추가
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(new java.util.Date())
                .setExpiration(new java.util.Date(System.currentTimeMillis() + Duration.ofDays(7).toMillis()))
                .signWith(io.jsonwebtoken.SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();

        // When: TokenProvider의 getUserId() 메서드로 ID 추출
        Long extractedMemberId = tokenProvider.getUserId(token);

        // Then: 추출한 회원 ID가 원래 회원 ID와 같은지 검증
        assertThat(extractedMemberId).isEqualTo(memberId);
    }
}
