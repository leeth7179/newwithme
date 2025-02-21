package com.javalab.student.config;

import com.javalab.student.config.jwt.RefreshTokenCheckFilter;
import com.javalab.student.config.jwt.TokenAuthenticationFilter;
import com.javalab.student.config.jwt.TokenProvider;
import com.javalab.student.security.CustomUserDetailsService;
import com.javalab.student.security.handler.CustomAuthenticationEntryPoint;
import com.javalab.student.security.handler.CustomAuthenticationSuccessHandler;
import com.javalab.student.security.handler.CustomLogoutSuccessHandler;
import com.javalab.student.security.oauth.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 파일
 * - 인증, 권한 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Log4j2
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService; // 사용자 정보를 가져오는 역할
    private final CustomOAuth2UserService customOAuth2UserService;  // 소셜 로그인
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler; // 로그인 성공 핸들러
    private final TokenAuthenticationFilter tokenAuthenticationFilter; // 토큰을 검증하고 인증 객체를 SecurityContext에 저장하는 역할
    private final TokenProvider tokenProvider;  // 토큰 생성 및 검증
    private final RefreshTokenCheckFilter refreshTokenCheckFilter; // 추가된 필터
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler; // 로그아웃 성공 핸들러

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.formLogin(form -> form
                .loginPage("/api/auth/login")
                .loginProcessingUrl("/api/auth/login")
                .successHandler(customAuthenticationSuccessHandler)
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"error\":\"로그인에 실패했습니다.\"}");
                })
                .permitAll()
        );

        http.logout(logout -> logout
                .logoutUrl("/api/auth/logout")
                .logoutSuccessHandler(customLogoutSuccessHandler)
                .permitAll()
        );

        // CORS 설정
        http.cors(Customizer.withDefaults());   // WebConfig의 CORS 설정을 사용

        // URL 접근 권한 설정
        http.authorizeHttpRequests(request -> request
                .requestMatchers("/ws/**").permitAll()
                .requestMatchers("/topic/**").permitAll()

                // 문진 관련 API
                .requestMatchers("/api/questionnaires/free").permitAll()
                .requestMatchers("/api/questionnaires/{questionnaireId}").permitAll()
                .requestMatchers("/api/questionnaires/user/{userId}").permitAll()

                // 유료 문진 관련 API (ROLE_VIP만 접근 가능)
                .requestMatchers("/api/survey-topics/paid/**").hasRole("VIP")
                .requestMatchers("/api/user-selected-topics/**").hasRole("VIP")

                //    WebSocket 접속이 정상인지 체크하는 핸드쉐이크 요청인 /ws/info와 WebSocket 연결, /ws/**는 인증 없이 접근할 수 있도록 설정합니다.
                .requestMatchers("/ws/**").permitAll()  //
                .requestMatchers("/api/questionnaires/free").permitAll()
                .requestMatchers("/topic/**").permitAll()  // ✅ STOMP 메시지 브로커 경로 허용
                .requestMatchers("/", "/api/auth/login", "/api/auth/logout", "/api/members/register", "/api/members/checkEmail","/api/auth/login/kakao").permitAll() // 로그인 API 허용 [수정]
                .requestMatchers(HttpMethod.GET, "/api/notices/**","/api/posts/**","/api/comment/**","/api/posts/*/comments").permitAll() // GET 요청은 모든 사용자에게 허용
                //.requestMatchers("/api/posts/**", "/api/comments/**","/api/posts/*/comments/**").authenticated() //인증 필요
                .requestMatchers(HttpMethod.POST, "/api/posts", "/api/comments","/api/posts/*/comments").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/posts/**", "/api/comments/**","/api/posts/*/comments/**").authenticated()
               .requestMatchers(HttpMethod.DELETE, "/api/posts/**", "/api/comments/**","/api/posts/*/comments/**").authenticated()
                // 공지사항 등록, 수정, 삭제는 ADMIN만 접근 가능
                .requestMatchers(HttpMethod.POST, "/api/notices/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/notices/**").hasRole("ADMIN")

                // WebSocket 설정
                .requestMatchers("/ws/**", "/topic/**").permitAll()

                // 인증 및 회원 관련 API
                .requestMatchers("/", "/api/auth/login", "/api/auth/logout", "/api/members/register", "/api/members/checkEmail").permitAll()
                .requestMatchers("/api/auth/userInfo").permitAll()
                .requestMatchers("/api/members/**").hasAnyRole("USER", "ADMIN", "VIP", "DOCTOR")
                // 인증 불필요 API
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/logout",
                    "/api/members/register",
                    "/api/members/checkEmail",
                    "/api/auth/login/kakao",
                    "/api/items/search/**",
                    "/api/substances/list"
                ).permitAll()

                // GET 요청 허용
                .requestMatchers(HttpMethod.GET,
                        "/api/notices/**",
                         "/api/posts/**",
                        "/api/posts/*/comments",
                         "/api/comments/**",
                        "/api/members/{userId}/comments",
                        "/api/pets/user/{userId}",
                        "/api/posts/user/{userId}",
                        "/api/posts/comments/user/{userId}",
                        "/api/pets/image/**",
                        "/api/post/image/**",
                        "/api/item/list",
                        "/api/item/view/**"
                ).permitAll()

                // 반려동물 관련
                .requestMatchers(HttpMethod.POST, "/api/pets/register").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/pets/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/pets/**").authenticated()
                .requestMatchers("/api/pets/{petId}").authenticated()

                // 관리자 전용
                .requestMatchers(HttpMethod.POST, "/api/notices/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/notices/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/notices/**").hasRole("ADMIN")
                .requestMatchers("/api/students/**").hasRole("ADMIN")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/members/**").hasAnyRole("USER", "ADMIN","VIP","DOCTOR","PENDING_DOCTOR") // 사용자 정보 수정 API는 USER, ADMIN만 접근 가능
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()  // 스웨거 Swagger UI는 인증을 거치지 않고 접근 가능
                .requestMatchers("/api/messages/**").hasAnyRole("USER", "ADMIN","VIP","DOCTOR","PENDING_DOCTOR") // 사용자의 읽지 않은 메시지 개수 조회 API는 USER, ADMIN만 접근 가능
                .requestMatchers("/api/questions/**").hasAnyRole("USER", "ADMIN","VIP","DOCTOR","PENDING_DOCTOR")
                .requestMatchers("/api/chat/**").hasAnyRole("USER", "ADMIN","VIP","DOCTOR","PENDING_DOCTOR") // 채팅방 생성, 채팅방 목록 조회 API는 USER, ADMIN만 접근 가능
                // 쇼핑몰
                .requestMatchers("/api/item/list", "/api/item/view/**").permitAll()
                .requestMatchers("/api/item/new", "/api/item/edit/**","/api/item/delete/**").hasRole("ADMIN")
                .requestMatchers("/api/cart/**","/api/orders/**").authenticated()
                .requestMatchers("/api/payments/**").authenticated() // 결제

                //커뮤니티
                //커뮤니티 이미지 업로드
                .requestMatchers(HttpMethod.POST, "/api/posts/upload").permitAll()
                //커뮤니티 인증된 사용자 전용
                .requestMatchers(HttpMethod.POST, "/api/posts", "/api/posts/*/comments").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/posts/**", "/api/posts/*/comments/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/posts/**", "/api/posts/*/comments/**").authenticated()

                // 특정 역할 사용자
                .requestMatchers("/api/members/**").authenticated()
                .requestMatchers("/api/messages/**", "/api/chat/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/api/cart/**","/api/orders/**").authenticated()

                // API 문서 및 의사 관련
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers("/api/doctors/**").permitAll()
                .requestMatchers("/api/auth/userInfo").permitAll()

                // 메시지 및 커뮤니티 관련 API
                .requestMatchers("/api/messages/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")
                .requestMatchers("/api/chat/**").hasAnyAuthority("ROLE_USER", "ROLE_ADMIN")



                // 문진 관련 API - 더 구체적인 패턴을 먼저 배치
                .requestMatchers("/api/questions/free/latest/{userId}").hasAuthority("ROLE_USER")
                .requestMatchers("/api/questions/paid/latest/{userId}").hasAuthority("ROLE_VIP")
                .requestMatchers("/api/questions/free/**").hasAuthority("ROLE_USER")
                .requestMatchers("/api/questions/paid/**").hasAuthority("ROLE_VIP")
                .requestMatchers("/api/questions").hasAnyAuthority("ROLE_USER", "ROLE_VIP")

                // 설문 주제 및 사용자 선택 주제 관련 API
                .requestMatchers("/api/survey-topics/paid/**").hasAuthority("ROLE_VIP")
                .requestMatchers("/api/survey-topics/**").hasAnyAuthority("ROLE_USER", "ROLE_VIP")
                .requestMatchers("/api/user-selected-topics/**").hasAnyAuthority("ROLE_USER", "ROLE_VIP")

                // 관리자 관련 API
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // 학생 관련 API
                .requestMatchers(HttpMethod.GET, "/api/students/**").permitAll()
                .requestMatchers("/api/students/**").hasRole("ADMIN")

                // 의사 관련 API
                .requestMatchers("/api/doctors/**").permitAll()

                // 메시지 및 커뮤니티 관련 API
                .requestMatchers("/api/messages/**").hasAnyRole("USER", "ADMIN", "VIP", "DOCTOR")
                .requestMatchers("/api/questions/**").hasAnyRole("USER", "VIP")
                .requestMatchers("/api/chat/**").hasAnyRole("USER", "ADMIN")

                // 정적 리소스 허용
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                .requestMatchers(
                        "/images/**",
                        "/image/**",
                        "/static-images/**",
                        "/css/**",
                        "/img/**",
                        "/favicon.ico",
                        "/error",
                        "/**/*.css",
                        "/**/*.js",
                        "/**/*.png",
                        "/**/*.jpg",
                        "/**/*.jpeg",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.html",
                        "/ping.js"
                ).permitAll()

                .anyRequest().authenticated()
        );

        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(refreshTokenCheckFilter, TokenAuthenticationFilter.class);

        http.exceptionHandling(exception -> exception
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
        );

        http.csrf(csrf -> csrf.disable());
        http.cors(Customizer.withDefaults());

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/api/auth/login/kakao")
                .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
        );

        return http.build();
    }

    /**
     * AuthenticationManager 빈 등록
     */
    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder())
                .and()
                .build();
    }

    /**
     * 비밀번호 암호화를 위한 PasswordEncoder 빈 등록
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}