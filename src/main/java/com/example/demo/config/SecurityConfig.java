package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration // 스프링 설정 클래스 등록
@EnableWebSecurity // 스프링 시큐리티 활성화
@EnableMethodSecurity // [6주차] 메서드 보안 활성화 : @PreAuthorize 사용 (없으면 그냥 무시된다)
public class SecurityConfig {

    @Bean // 비밀번호 암호화 객체 등록 (BCrypt 해시)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean // 보안 필터 체인 등록
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth // 1. URL 접근 규칙 (인가) : 위에서부터 순서대로 검사
                .requestMatchers("/", "/hello", "/hello2", "/detailed_web.html",
                                 "/login", "/signup", "/error").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/fonts/**").permitAll() // 빠지면 디자인 깨짐
                .requestMatchers("/api/hello").permitAll() // [7주차] 첫 번째 API : 누구나
                // [9주차] 게시판 목록·상세 : 누구나 ({id:\d+} = 숫자만 → /board/write 는 포함 안 됨, 로그인 필요)
                .requestMatchers("/board", "/board/{id:\\d+}").permitAll()
                // [6주차] 관리자만 (1차 잠금, anyRequest 보다 위에!) + [7주차] 새 주소를 만들면 접근 규칙도 바로 추가
                .requestMatchers("/admin/**", "/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()) // 나머지 전부 로그인 필요 → /testdb, /mypage
            .formLogin(form -> form // 2. 폼 로그인 설정 (인증)
                .loginPage("/login") // 기본 화면 대신 우리 로그인 화면
                .defaultSuccessUrl("/") // 성공 시 메인 (원래 가려던 곳 우선)
                .failureUrl("/login?error") // 실패 시 에러 파라미터
                .permitAll())
            .logout(logout -> logout // 3. 로그아웃 설정
                .logoutUrl("/logout") // 로그아웃 처리 URL (POST)
                .logoutSuccessUrl("/login?logout") // 로그아웃 후 이동
                .invalidateHttpSession(true) // 세션 삭제
                .deleteCookies("JSESSIONID", "remember-me")) // 쿠키 삭제
            // [5주차 연습문제 ①] 로그인 상태 유지 : 여기에 .rememberMe(...) 를 이어서 추가해 보세요.
            // 5. [7주차] REST API 설정
            .httpBasic(Customizer.withDefaults()) // Postman : 요청마다 아이디·비밀번호 (Basic 인증)
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**")) // JSON API 는 CSRF 토큰 검사 제외
            .exceptionHandling(ex -> ex.defaultAuthenticationEntryPointFor( // 로그인 안 한 API 요청 :
                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), // 로그인 화면(302) 대신 401
                request -> request.getRequestURI().startsWith("/api/")));
        return http.build();
    }
}
