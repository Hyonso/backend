package com.boterview.interview_api.security.api.controller;

import com.boterview.interview_api.security.api.dto.*;
import com.boterview.interview_api.security.api.service.AuthService;
import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    private static final String REFRESH_TOKEN_COOKIE_NAME = "refreshToken";
    private static final int REFRESH_TOKEN_MAX_AGE = 7 * 24 * 60 * 60; // 7일

    /**
     * 로그인 API
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @RequestBody LoginRequest request,
            HttpServletResponse response) {

        JwtInformation jwtInfo = authService.login(request.getEmail(), request.getPassword());

        // RefreshToken을 HttpOnly 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, jwtInfo.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true); // HTTPS에서만 전송
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        response.addCookie(refreshTokenCookie);

        // AccessToken과 사용자 정보 반환
        LoginResponse loginResponse = new LoginResponse(
                jwtInfo.getAccessToken(),
                new LoginResponse.UserInfo(
                        jwtInfo.getUserDto().getUserId(),
                        jwtInfo.getUserDto().getName(),
                        jwtInfo.getUserDto().getEmail()));

        return ResponseEntity.ok(loginResponse);
    }

    /**
     * 회원가입 API
     * POST /api/auth/signup
     */
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest request) {
        String userId = authService.signup(
                request.getEmail(),
                request.getPassword(),
                request.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new SignupResponse(userId));
    }

    /**
     * 토큰 재발급 API
     * POST /api/auth/refresh
     */
    @PostMapping("/refresh")
    public ResponseEntity<RefreshTokenResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {

        JwtInformation newJwtInfo = authService.refreshToken(refreshToken);

        // 새 RefreshToken을 쿠키로 설정
        Cookie refreshTokenCookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, newJwtInfo.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(REFRESH_TOKEN_MAX_AGE);
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(new RefreshTokenResponse(newJwtInfo.getAccessToken()));
    }

    /**
     * 로그아웃 API
     * POST /api/auth/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(name = REFRESH_TOKEN_COOKIE_NAME, required = false) String refreshToken,
            HttpServletResponse response) {

        authService.logout(refreshToken);

        // RefreshToken 쿠키 삭제
        Cookie cookie = new Cookie(REFRESH_TOKEN_COOKIE_NAME, null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0); // 즉시 삭제
        response.addCookie(cookie);

        return ResponseEntity.noContent().build(); // 204 No Content
    }

    /**
     * 비밀번호 재설정 API
     * POST /api/auth/password/forgot
     */
    @PostMapping("/password/forgot")
    public ResponseEntity<Map<String, Boolean>> forgotPassword(@RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getEmail());
        return ResponseEntity.ok(Map.of("resetSent", true));
    }
}
