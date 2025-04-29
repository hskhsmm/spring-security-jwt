package com.example.Springjwt.repository;

import com.example.Springjwt.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    //existby 쿼리 절을 사용. username을 기반으로.
    Boolean existsByUsername(String username);
}
