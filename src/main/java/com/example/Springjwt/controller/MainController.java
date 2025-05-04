package com.example.Springjwt.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Iterator;

@RestController
public class MainController {

    @GetMapping("/")
    public String mainP() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return "인증되지 않은 사용자입니다.";
        }

        String username = authentication.getName();

        // 권한 정보 가져오기 (여러 개일 수 있음)
        StringBuilder roles = new StringBuilder();
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            roles.append(authority.getAuthority()).append(" ");
        }

        return "Main Controller: " + username + ", Role(s): " + roles.toString().trim();
    }

}
