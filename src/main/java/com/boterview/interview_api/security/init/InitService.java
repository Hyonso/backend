package com.boterview.interview_api.security.init;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InitService {

    @Value("${init.admin.password}")
    private String adminPassword;

    @Value("${init.admin.email}")
    private String adminEmail;

    @Value("${init.user.password}")
    private String userPassword;

    @Value("${init.user.email}")
    private String userEmail;

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public void initAdmin() {
        if (userMapper.findByEmail(adminEmail).isPresent()) {
            return;
        }

        User admin = User.builder()
                .userId(UUID.randomUUID().toString())
                .email(adminEmail)
                .password(passwordEncoder.encode(adminPassword))
                .name("관리자")
                .createdAt(LocalDateTime.now())
                .build();

        userMapper.insert(admin);
    }

    public void initDefaultUser() {
        if (userMapper.findByEmail(userEmail).isPresent()) {
            return;
        }

        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .email(userEmail)
                .password(passwordEncoder.encode(userPassword))
                .name("테스트 사용자")
                .createdAt(LocalDateTime.now())
                .build();

        userMapper.insert(user);
    }
}
