package com.example.Springjwt.service;

import com.example.Springjwt.dto.JoinDTO;
import com.example.Springjwt.entity.UserEntity;
import com.example.Springjwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor // 생성자 주입을 자동으로 만들어주는 Lombok 어노테이션
public class JoinService {

    private final UserRepository userRepository; // 사용자 정보를 다루는 JPA Repository
    private final BCryptPasswordEncoder bCryptPasswordEncoder; // 비밀번호 암호화를 위한 Bean

    /**
     * 회원가입 처리를 위한 메서드
     * @param joinDTO 클라이언트로부터 전달받은 회원가입 정보(username, password)
     */
    public void joinProcess(JoinDTO joinDTO) {

        // DTO에서 username과 password 추출
        String username = joinDTO.getUsername();
        String password = joinDTO.getPassword();

        // 이미 존재하는 사용자명인지 확인
        Boolean isExists = userRepository.existsByUsername(username);

        if (isExists) {
            // username이 이미 존재하면 회원가입 중단
            return;
        }

        // 새로운 사용자 엔티티 생성
        UserEntity data = new UserEntity();

        // 사용자명 설정
        data.setUsername(username);

        // 비밀번호를 암호화하여 설정
        data.setPassword(bCryptPasswordEncoder.encode(password));

        // 기본 권한 설정 (예: ROLE_ADMIN)
        data.setRole("ROLE_ADMIN");

        // 회원 정보(3개 필드: username, password, role)를 DB에 저장
        userRepository.save(data);
    }
}
