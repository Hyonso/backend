package com.boterview.interview_api.security.api.controller;

import com.boterview.interview_api.security.api.dto.*;
import com.boterview.interview_api.security.api.service.AuthService;
import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;
import com.boterview.interview_api.security.authentication.jwt.registry.TestJwtRegistryConfig;
import com.boterview.interview_api.security.core.dto.SecurityUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 통합 테스트
 * REST API 엔드포인트 테스트 (실제 서비스 레이어 연동)
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({ TestJwtRegistryConfig.class, com.boterview.interview_api.config.TestSecurityConfig.class })
@Transactional
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private AuthService authService;

        private String testEmail = "authtest@example.com";
        private String testPassword = "Testpassword123!";
        private String testName = "Auth Test User";

        @BeforeEach
        void setUp() {
                // 각 테스트마다 독립적으로 데이터 준비
                // @Transactional 덕분에 테스트 후 롤백됨
        }

        @Test
        @DisplayName("POST /api/auth/signup → /api/auth/login - 성공")
        void signupAndLogin_Success() throws Exception {
                // 1. 회원가입
                SignupRequest signupRequest = new SignupRequest(testEmail, testPassword, testName);
                mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signupRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.userId").isNotEmpty());

                // 2. 로그인
                LoginRequest loginRequest = new LoginRequest(testEmail, testPassword);
                mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                                .andExpect(jsonPath("$.user.email").value(testEmail))
                                .andExpect(cookie().exists("refreshToken"));
        }

        @Test
        @DisplayName("POST /api/auth/signup - 실패 (이메일 중복)")
        void signup_Failure_DuplicateEmail() throws Exception {
                // Given - 먼저 회원가입
                SignupRequest firstRequest = new SignupRequest(testEmail, testPassword, testName);
                authService.signup(testEmail, testPassword, testName);

                // When & Then - 같은 이메일로 재가입 시도
                mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(firstRequest)))
                                .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("POST /api/auth/refresh - 성공")
        void refresh_Success() throws Exception {
                // Given - 사용자 생성 후 로그인
                authService.signup(testEmail, testPassword, testName);
                JwtInformation initialJwt = authService.login(testEmail, testPassword);

                // When & Then - 토큰 갱신
                mockMvc.perform(post("/api/auth/refresh")
                                .cookie(new jakarta.servlet.http.Cookie("refreshToken", initialJwt.getRefreshToken())))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").isNotEmpty());
        }

        @Test
        @DisplayName("POST /api/auth/logout - 성공")
        void logout_Success() throws Exception {
                // Given - 사용자 생성 후 로그인
                authService.signup(testEmail, testPassword, testName);
                JwtInformation jwtInfo = authService.login(testEmail, testPassword);

                // When & Then - 로그아웃
                mockMvc.perform(post("/api/auth/logout")
                                .cookie(new jakarta.servlet.http.Cookie("refreshToken", jwtInfo.getRefreshToken())))
                                .andExpect(status().isNoContent())
                                .andExpect(cookie().maxAge("refreshToken", 0));
        }

        @Test
        @DisplayName("POST /api/auth/password/forgot - 성공")
        void forgotPassword_Success() throws Exception {
                // Given - 사용자 생성
                authService.signup(testEmail, testPassword, testName);

                // When & Then - 비밀번호 재설정
                ResetPasswordRequest request = new ResetPasswordRequest(testEmail);
                mockMvc.perform(post("/api/auth/password/forgot")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().isOk());
        }
}
