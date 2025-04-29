package com.example.Springjwt.controller;

import com.example.Springjwt.dto.JoinDTO;
import com.example.Springjwt.service.JoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 해당 클래스가 REST API의 컨트롤러임을 명시 (JSON 형태로 응답)
@RequiredArgsConstructor // final 필드를 대상으로 생성자 자동 생성 (DI 자동 처리)
public class JoinController {

    private final JoinService joinService; // 회원가입 처리를 위한 서비스 클래스 의존성 주입

    /**
     * 회원가입 요청을 처리하는 POST 메서드
     * @param joinDTO 클라이언트로부터 전달받은 회원가입 데이터 (username, password)
     * @return 회원가입 성공 시 "ok" 문자열 반환
     */
    @PostMapping("/join") // HTTP POST 요청 시 "/join" 경로로 매핑
    public String joinProcess(JoinDTO joinDTO) {
        joinService.joinProcess(joinDTO); // 서비스에 회원가입 로직 위임
        return "ok"; // 응답 메시지
    }
}
