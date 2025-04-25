package com.example.Springjwt.config;

// 시큐리티 설정에 필요한 클래스들을 import
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration // 이 클래스가 설정 클래스라는 걸 Spring에게 알림
@EnableWebSecurity // Spring Security 활성화 어노테이션
public class SecurityConfig {

    // 비밀번호를 암호화하는 데 사용하는 BCryptPasswordEncoder를 Bean으로 등록
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 시큐리티 필터 체인을 설정하는 Bean
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // 1. CSRF 보안 기능 비활성화
        // JWT를 사용하는 경우, 세션/쿠키 기반이 아니라서 CSRF 보호가 필요 없음
        http.csrf((auth) -> auth.disable());

        // 2. 기본 제공 로그인 폼 기능 비활성화
        // 우리는 REST API 방식으로 로그인할 예정이므로 formLogin은 꺼준다
        http.formLogin((auth) -> auth.disable());

        // 3. HTTP Basic 인증 방식 비활성화
        // 브라우저 팝업창 로그인 방식도 사용하지 않을 예정이므로 꺼준다
        http.httpBasic((auth) -> auth.disable());

        // 4. URL 접근 권한 설정
        http.authorizeHttpRequests((auth) -> auth
                // 로그인, 회원가입, 메인 페이지는 인증 없이 접근 가능
                .requestMatchers("/login", "/", "/join").permitAll()
                // /admin 경로는 ADMIN 권한이 있어야 접근 가능
                .requestMatchers("/admin").hasRole("ADMIN")
                // 위에 명시되지 않은 모든 요청은 인증이 필요함
                .anyRequest().authenticated()
        );

        // 5. 세션 관리 정책 설정
        // JWT를 사용할 경우 서버에 세션 정보를 저장하지 않으므로 STATELESS로 설정
        http.sessionManagement((session) ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        // 모든 설정을 적용한 SecurityFilterChain 객체를 반환
        return http.build();
    }
}
