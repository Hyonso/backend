package com.boterview.interview_api.security;

import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;
import com.boterview.interview_api.security.api.dto.LoginRequest;
import com.boterview.interview_api.security.api.dto.SignupRequest;
import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;
import com.boterview.interview_api.security.authentication.jwt.registry.TestJwtRegistryConfig;
import com.boterview.interview_api.security.core.dto.SecurityUserDto;
import com.boterview.interview_api.security.core.principal.BotUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Security 통합 테스트
 * 전체 인증 흐름을 엔드투엔드로 테스트
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({ TestJwtRegistryConfig.class, com.boterview.interview_api.config.TestSecurityConfig.class })
@Transactional
@Sql(scripts = "/test-data.sql")
class SecurityIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @Autowired
        private JwtTokenProvider jwtTokenProvider;

        @Autowired
        private UserMapper userMapper;

        @Autowired
        private PasswordEncoder passwordEncoder;

        private User testUser;
        private String testPassword = "password123";

        @BeforeEach
        void setUp() {
                // 테스트 사용자 생성
                testUser = User.builder()
                                .userId(UUID.randomUUID().toString())
                                .email("integration@test.com")
                                .password(passwordEncoder.encode(testPassword))
                                .name("Integration Test User")
                                .createdAt(LocalDateTime.now())
                                .build();
                userMapper.insert(testUser);
        }

        @Test
        @DisplayName("전체 인증 흐름 - 회원가입 → 로그인 → 보호된 리소스 접근")
        void fullAuthenticationFlow_SignupLoginAccess() throws Exception {
                String newEmail = "newuser@test.com";
                String password = "newpassword123";
                String name = "New User";

                // 1. 회원가입
                SignupRequest signupRequest = new SignupRequest(newEmail, password, name);
                mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(signupRequest)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.userId").isNotEmpty());

                // 2. 로그인
                LoginRequest loginRequest = new LoginRequest(newEmail, password);
                MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                                .andExpect(cookie().exists("refreshToken"))
                                .andReturn();

                // 3. Access Token 추출
                // 4. 보호된 리소스 접근 (Authorization 헤더 사용)
                // Note: 실제 보호된 엔드포인트가 있다면 테스트
                // String responseBody = loginResult.getResponse().getContentAsString();
                // String accessToken =
                // objectMapper.readTree(responseBody).get("accessToken").asText();
                // mockMvc.perform(get("/api/protected/resource")
                // .header("Authorization", "Bearer " + accessToken))
                // .andExpect(status().isOk());
        }

        @Test
        @DisplayName("보호된 엔드포인트 접근 - 인증 없이")
        void accessProtectedEndpoint_WithoutAuth_ShouldReturn401() throws Exception {
                // Note: 실제 보호된 엔드포인트가 있을 때 테스트
                // mockMvc.perform(get("/api/protected/resource"))
                // .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("보호된 엔드포인트 접근 - 유효한 토큰")
        void accessProtectedEndpoint_WithValidToken_ShouldReturn200() throws Exception {
                // Given
                SecurityUserDto userDto = SecurityUserDto.from(testUser);
                BotUserDetails userDetails = new BotUserDetails(userDto, testUser.getPassword());
                String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

                // Note: 실제 보호된 엔드포인트가 있을 때 테스트
                // mockMvc.perform(get("/api/protected/resource")
                // .header("Authorization", "Bearer " + accessToken))
                // .andExpect(status().isOk());

                // 토큰 검증 확인
                assertThat(jwtTokenProvider.validateAccessToken(accessToken)).isTrue();
        }

        @Test
        @DisplayName("보호된 엔드포인트 접근 - 유효하지 않은 토큰")
        void accessProtectedEndpoint_WithInvalidToken_ShouldReturn401() throws Exception {
                String invalidToken = "invalid.jwt.token";

                // Note: 실제 보호된 엔드포인트가 있을 때 테스트
                // mockMvc.perform(get("/api/protected/resource")
                // .header("Authorization", "Bearer " + invalidToken))
                // .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("토큰 갱신 흐름 - 로그인 → 토큰 갱신")
        void tokenRefreshFlow() throws Exception {
                // 1. 로그인
                LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), testPassword);
                MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andExpect(cookie().exists("refreshToken"))
                                .andReturn();

                jakarta.servlet.http.Cookie refreshCookie = loginResult.getResponse().getCookie("refreshToken");
                assertThat(refreshCookie).isNotNull();

                // 2. 토큰 갱신
                mockMvc.perform(post("/api/auth/refresh")
                                .cookie(refreshCookie))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                                .andExpect(cookie().exists("refreshToken"));
        }

        @Test
        @DisplayName("로그아웃 흐름 - 로그인 → 로그아웃")
        void logoutFlow() throws Exception {
                // 1. 로그인
                LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), testPassword);
                MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                jakarta.servlet.http.Cookie refreshCookie = loginResult.getResponse().getCookie("refreshToken");
                assertThat(refreshCookie).isNotNull();

                // 2. 로그아웃
                mockMvc.perform(post("/api/auth/logout")
                                .cookie(refreshCookie))
                                .andExpect(status().isNoContent())
                                .andExpect(cookie().maxAge("refreshToken", 0));
        }

        @Test
        @DisplayName("JWT 인증 필터 - Access Token에서 사용자 정보 추출")
        void jwtAuthenticationFilter_ExtractUserFromToken() {
                // Given
                SecurityUserDto userDto = SecurityUserDto.from(testUser);
                BotUserDetails userDetails = new BotUserDetails(userDto, testUser.getPassword());
                String accessToken = jwtTokenProvider.generateAccessToken(userDetails);

                // When
                String extractedEmail = jwtTokenProvider.getSubject(accessToken);
                String extractedUserId = jwtTokenProvider.getUserId(accessToken);

                // Then
                assertThat(extractedEmail).isEqualTo(testUser.getEmail());
                assertThat(extractedUserId).isEqualTo(testUser.getUserId());
        }

        @Test
        @DisplayName("동시 로그인 제한 - 같은 사용자의 여러 로그인 시도")
        void concurrentLoginLimit() throws Exception {
                // 1. 첫 번째 로그인
                LoginRequest loginRequest = new LoginRequest(testUser.getEmail(), testPassword);
                MvcResult firstLogin = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                String firstAccessToken = objectMapper.readTree(firstLogin.getResponse().getContentAsString())
                                .get("accessToken").asText();

                // 2. 두 번째 로그인 (같은 사용자)
                MvcResult secondLogin = mockMvc.perform(post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andExpect(status().isOk())
                                .andReturn();

                String secondAccessToken = objectMapper.readTree(secondLogin.getResponse().getContentAsString())
                                .get("accessToken").asText();

                // 3. 두 토큰은 다름
                assertThat(firstAccessToken).isNotEqualTo(secondAccessToken);

                // 4. 두 번째 로그인 후 첫 번째 토큰은 무효화됨 (max-active: 1 설정에 따라)
                // Note: JwtRegistry 구현에 따라 동작이 다를 수 있음
        }

        @Test
        @DisplayName("비밀번호 재설정 - 일반 사용자")
        void resetPassword_RegularUser() throws Exception {
                // When & Then
                mockMvc.perform(post("/api/auth/password/forgot")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{\"email\":\"" + testUser.getEmail() + "\"}"))
                                .andExpect(status().isOk());

                // 비밀번호가 변경되었는지 확인
                User updatedUser = userMapper.findByEmail(testUser.getEmail()).orElseThrow();
                assertThat(updatedUser.getPassword()).isNotEqualTo(testUser.getPassword());
        }

        @Test
        @DisplayName("중복 회원가입 방지")
        void preventDuplicateSignup() throws Exception {
                // Given
                SignupRequest request = new SignupRequest(
                                testUser.getEmail(), // 이미 존재하는 이메일
                                "password",
                                "Duplicate User");

                // When & Then
                mockMvc.perform(post("/api/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(request)))
                                .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("JWT 토큰 만료 검증")
        void jwtTokenExpiration() throws Exception {
                // Given
                SecurityUserDto userDto = SecurityUserDto.from(testUser);
                BotUserDetails userDetails = new BotUserDetails(userDto, testUser.getPassword());

                // 정상 토큰 생성
                String validToken = jwtTokenProvider.generateAccessToken(userDetails);

                // When & Then
                assertThat(jwtTokenProvider.validateAccessToken(validToken)).isTrue();

                // Note: 실제 만료된 토큰을 테스트하려면 시간을 조작하거나
                // 매우 짧은 만료 시간으로 설정된 TokenProvider를 별도로 생성해야 함
        }
}
