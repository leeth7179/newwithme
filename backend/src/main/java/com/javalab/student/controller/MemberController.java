package com.javalab.student.controller;

import com.javalab.student.config.jwt.TokenProvider;
import com.javalab.student.constant.Role;
import com.javalab.student.dto.*;
import com.javalab.student.entity.Member;
import com.javalab.student.repository.MemberRepository;
import com.javalab.student.service.MemberService;
import com.javalab.student.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.javalab.student.config.jwt.TokenProvider;
import com.javalab.student.dto.*;
import com.javalab.student.entity.Member;
import com.javalab.student.service.MemberService;
import com.javalab.student.service.RefreshTokenService;

import jakarta.servlet.http.Cookie;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;

    /**
     * 일반 회원가입 처리
     * @param memberFormDto - 클라이언트에서 전송한 회원가입 데이터
     * @return 성공 또는 실패 메시지를 포함한 JSON 응답
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerMember(@Valid @RequestBody MemberFormDto memberFormDto) {
        Map<String, Object> response = new HashMap<>();
        try {
            memberService.registerMember(memberFormDto);
            response.put("status", "success");
            response.put("message", "회원가입이 완료되었습니다.");
            return ResponseEntity.ok(response); // HTTP 200 OK
        } catch (IllegalStateException e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response); // HTTP 400 Bad Request
        }
    }

    /**
     * ID로 사용자 정보 조회
     * @param id - 사용자 ID
     * @return 사용자 정보 또는 에러 메시지를 포함한 JSON 응답
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getMemberById(@PathVariable("id") Long id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Member member = memberService.findById(id); // 사용자 정보 조회
            if (member == null) {
                response.put("status", "error");
                response.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            response.put("status", "success");
            response.put("data", member); // 사용자 정보를 data 키로 반환
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "서버 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    /*
        위 메소드의 반환결과가 다음과 같다.
        {
          "status": "success",
          "data": {
            "id": 102,
            "name": "김길동",
            "email": "test2@example.com",
            "phone": "010-2222-3333",
            "address": "서울시 강남구"
            // 기타 사용자 정보
          }
        }
     */

    /**
     * 사용자 정보 수정
     * @param id - 사용자 ID
     * @param memberFormDto - 수정할 사용자 정보
     * @return 성공 또는 실패 메시지를 포함한 JSON 응답
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateMember(@PathVariable("id") Long id,
                                                            @Valid @RequestBody MemberFormDto memberFormDto,
                                                            HttpServletResponse response) {
        Map<String, Object> responseBody = new HashMap<>();
        try {
            // 1. 사용자 정보 DB 업데이트
            memberService.updateMember(id, memberFormDto);

            // 2. 업데이트된 사용자 정보 조회
            Member updatedMember = memberService.findById(id);
            if (updatedMember == null) {
                responseBody.put("status", "error");
                responseBody.put("message", "사용자를 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseBody);
            }

            // 3. 새로운 액세스 토큰 생성
            String newAccessToken = tokenProvider.generateToken(
                    updatedMember.getEmail(),
                    //updatedMember.getAuthorities(),
                    //updatedMember.getName(),
                    Duration.ofMinutes(1) // 액세스 토큰 유효 기간
            );

            // 4. 새로운 리프레시 토큰 생성
            String newRefreshToken = tokenProvider.generateRefreshToken(
                    updatedMember.getEmail(),
                    Duration.ofDays(7) // 리프레시 토큰 유효 기간
            );

            // 5. 리프레시 토큰 DB 저장 또는 업데이트
            refreshTokenService.saveOrUpdateRefreshToken(updatedMember.getEmail(), newRefreshToken);

            // 6. 액세스 토큰을 HttpOnly 쿠키로 설정
            Cookie accessTokenCookie = new Cookie("accToken", newAccessToken);
            accessTokenCookie.setHttpOnly(true);
            accessTokenCookie.setSecure(false); // HTTPS 환경에서 true로 설정
            accessTokenCookie.setPath("/");
            response.addCookie(accessTokenCookie);

            // 7. 리프레시 토큰을 HttpOnly 쿠키로 설정
            Cookie refreshTokenCookie = new Cookie("refToken", newRefreshToken);
            refreshTokenCookie.setHttpOnly(true);
            refreshTokenCookie.setSecure(false); // HTTPS 환경에서 true로 설정
            refreshTokenCookie.setPath("/refresh");
            response.addCookie(refreshTokenCookie);

            // 8. 사용자 정보와 상태 반환
            responseBody.put("status", "success");
            responseBody.put("message", "사용자 정보가 수정되고 JWT가 갱신되었습니다.");
            responseBody.put("id", updatedMember.getId());
            responseBody.put("email", updatedMember.getEmail());
            responseBody.put("name", updatedMember.getName());
            responseBody.put("roles", updatedMember.getAuthorities());
            return ResponseEntity.ok(responseBody);
        } catch (IllegalArgumentException e) {
            responseBody.put("status", "error");
            responseBody.put("message", "잘못된 요청: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        } catch (Exception e) {
            responseBody.put("status", "error");
            responseBody.put("message", "서버 오류 발생");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    /**
     * 이메일 중복 체크
     * @param email - 클라이언트에서 입력받은 이메일
     * @return 중복 여부
     */
    /**
     * 이메일 중복 체크
     * @param email - 클라이언트에서 입력받은 이메일
     * @return 중복 여부 (200 OK로 항상 응답, 상태 값으로 판단)
     */
    @GetMapping("/checkEmail")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam("email") String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            boolean isDuplicate = memberService.isEmailDuplicate(email);

            if (isDuplicate) {
                response.put("status", "duplicate");
                response.put("message", "이미 존재하는 이메일입니다.");
            } else {
                response.put("status", "available");
                response.put("message", "사용 가능한 이메일입니다.");
            }

            return ResponseEntity.ok(response); // 항상 200 OK 반환
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "이메일 중복 체크 중 오류 발생: " + e.getMessage());
            return ResponseEntity.ok(response); // 오류도 200 OK로 응답
        }
    }

    /**
     * 로그인 처리[미사용-일반 시큐리티 로그인]
     * @param loginForm - 클라이언트에서 전송한 로그인 데이터
     * @return 성공 메시지 또는 에러 메시지
     */
    @PostMapping("/login")
public ResponseEntity<?> login(@RequestBody LoginFormDto loginFormDto) {
    try {
        // Authenticate user
        Member member = memberService.authenticate(loginFormDto.getEmail(), loginFormDto.getPassword());

        // Generate JWT token
        String token = tokenProvider.generateToken(member.getEmail(), Duration.ofHours(2));

        // Build response
        Map<String, Object> response = new HashMap<>();
        response.put("id", member.getId());
        response.put("name", member.getName());
        response.put("email", member.getEmail());
        response.put("roles", member.getRole().name()); // Include role information
        response.put("token", token);

        return ResponseEntity.ok(response);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", e.getMessage()));
    }
}

   /* // 페이징 처리된 유저 목록 조회
    @GetMapping("/list")
    //@PreAuthorize("hasRole('USER')")
    public ResponseEntity<PageResponseDTO<MemberDto>> getAllmembers(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(page)
                .size(size)
                .build();
        log.info("페이지 : " + pageRequestDTO.getPage() + " " + pageRequestDTO.getSize());

        PageResponseDTO<MemberDto> responseDTO = memberService.getAllMembers(pageRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }*/

    @GetMapping("/list")
    public ResponseEntity<List<Member>> getAllMembers() {
        return ResponseEntity.ok(memberService.getMember());
    }

    /**
     * 멤버 권한 변경
     */
    @PatchMapping("/role/{email}")
    public ResponseEntity<?> changeRole(@PathVariable String email, @RequestParam Role role) {
        memberService.updateMemberRole(email, role);
        return ResponseEntity.ok().build();
    }

}
