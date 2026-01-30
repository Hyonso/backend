package com.boterview.interview_api.security.api.service;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;
import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;
import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;
import com.boterview.interview_api.security.core.exception.InValidAccessTokenException;
import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;
import com.boterview.interview_api.security.authentication.jwt.registry.JwtRegistry;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtRegistry jwtRegistry;
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public JwtInformation refreshToken(String refreshToken) {
        if (!tokenProvider.validateRefreshToken(refreshToken)
                || !jwtRegistry.hasActiveJwtInformationByRefreshToken(refreshToken)) {
            throw new InValidAccessTokenException();
        }

        String email = tokenProvider.getSubject(refreshToken);
        BotUserDetails userDetails = (BotUserDetails) userDetailsService.loadUserByUsername(email);

        String newAccessToken = tokenProvider.generateAccessToken(userDetails);
        String newRefreshToken = tokenProvider.generateRefreshToken(userDetails);

        JwtInformation newInfo = new JwtInformation(
                userDetails.getUserDto(),
                newAccessToken,
                newRefreshToken
        );

        jwtRegistry.rotateJwtInformation(refreshToken, newInfo);
        return newInfo;
    }

    public void signup(String email, String password, String name) {
        if (userMapper.findByEmail(email).isPresent()) {
            throw new BaseException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .userId(UUID.randomUUID().toString())
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .build();

        userMapper.insert(user);
    }

    public void resetPassword(String email) {
        User user = userMapper.findByEmail(email)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        if (user.getOauth() != null) {
            throw new BaseException(ErrorCode.AUTH_PASSWORD_ERROR);
        }

        String newPassword = UUID.randomUUID().toString().substring(0, 10);
        String encodedPassword = passwordEncoder.encode(newPassword);
        userMapper.updatePassword(user.getUserId(), encodedPassword);

        // TODO: 이메일 발송 기능 추가 시 여기에 구현
        log.info("임시 비밀번호가 생성되었습니다. email={}", email);
    }
}
