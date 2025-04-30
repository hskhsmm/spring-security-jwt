package com.example.Springjwt.config;

import com.example.Springjwt.jwt.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration // 이 클래스가 설정 파일임을 명시 (스프링 빈 등록 대상)
@EnableWebSecurity // Spring Security 활성화 (웹 보안 활성화)
public class SecurityConfig {

    // AuthenticationManager를 생성하기 위해 필요한 AuthenticationConfiguration을 주입받음
    private final AuthenticationConfiguration authenticationConfiguration;

    public SecurityConfig(AuthenticationConfiguration authenticationConfiguration) {
        this.authenticationConfiguration = authenticationConfiguration;
    }

    /**
     * AuthenticationManager를 Bean으로 등록
     * - Spring Security 5.7 이후에는 직접 AuthenticationManager를 설정해야 한다.
     * - LoginFilter에서 인증 처리를 위해 필요
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager(); // 내부적으로 ProviderManager 반환
    }

    /**
     * BCryptPasswordEncoder를 Bean으로 등록
     * - 비밀번호 암호화 및 매칭 시 사용됨
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * SecurityFilterChain 정의
     * - HTTP 요청에 대한 보안 설정의 핵심
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF 비활성화 (JWT 기반 인증이므로 필요 없음)
        http.csrf((auth) -> auth.disable());

        // form 로그인 방식 비활성화 (우리는 로그인 페이지를 제공하지 않음)
        http.formLogin((auth) -> auth.disable());

        // HTTP 기본 인증 비활성화 (Authorization 헤더에 ID/PW 보내는 방식 비활성화)
        http.httpBasic((auth) -> auth.disable());

        // 요청 URL에 따른 접근 권한 설정
        http.authorizeHttpRequests((auth) -> auth
                // 로그인, 회원가입, 루트 경로는 누구나 접근 가능
                .requestMatchers("/login", "/", "/join").permitAll()
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
        );

        // 커스텀 로그인 필터(LoginFilter)를 UsernamePasswordAuthenticationFilter 위치에 추가
        // LoginFilter는 JWT 인증을 처리하고, 인증 성공 시 토큰을 응답해줌
        http.addFilterAt(
                new LoginFilter(authenticationManager(authenticationConfiguration)),
                UsernamePasswordAuthenticationFilter.class
        );

        // 세션 사용하지 않음 (JWT 방식에서는 STATELESS 정책 설정 필수)
        http.sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build(); // 최종 보안 설정을 빌드하여 필터 체인 생성
    }
}
