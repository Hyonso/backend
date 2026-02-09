package com.boterview.interview_api.security.authentication.jwt.provider;

import com.boterview.interview_api.security.authentication.jwt.token.TokenType;
import com.boterview.interview_api.security.core.dto.SecurityUserDto;
import com.boterview.interview_api.security.core.principal.BotUserDetails;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jwt.SignedJWT;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.util.Date;

import static org.assertj.core.api.Assertions.*;

/**
 * JwtTokenProvider 단위 테스트
 * JWT 토큰 생성, 검증, 추출 로직 테스트
 */
class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;
    private BotUserDetails testUserDetails;

    private static final String TEST_ACCESS_SECRET = "test-access-secret-key-must-be-at-least-32-characters-long-for-testing";
    private static final String TEST_REFRESH_SECRET = "test-refresh-secret-key-must-be-at-least-32-characters-long-for-testing";
    private static final int ACCESS_TOKEN_EXPIRATION = 1800000; // 30분
    private static final int REFRESH_TOKEN_EXPIRATION = 604800000; // 7일

    @BeforeEach
    void setUp() throws JOSEException {
        jwtTokenProvider = new JwtTokenProvider(
                TEST_ACCESS_SECRET,
                ACCESS_TOKEN_EXPIRATION,
                TEST_REFRESH_SECRET,
                REFRESH_TOKEN_EXPIRATION,
                false // cookie secure = false for testing
        );

        SecurityUserDto userDto = SecurityUserDto.builder()
                .userId("test-user-123")
                .email("test@example.com")
                .name("Test User")
                .build();

        testUserDetails = new BotUserDetails(userDto, "encodedPassword");
    }

    @Test
    @DisplayName("Access Token 생성 - 유효한 클레임 포함")
    void generateAccessToken_ShouldContainValidClaims() throws ParseException {
        // When
        String accessToken = jwtTokenProvider.generateAccessToken(testUserDetails);

        // Then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken).isNotEmpty();

        SignedJWT signedJWT = SignedJWT.parse(accessToken);
        assertThat(signedJWT.getJWTClaimsSet().getSubject()).isEqualTo("test@example.com");
        assertThat(signedJWT.getJWTClaimsSet().getClaim("userId")).isEqualTo("test-user-123");
        assertThat(signedJWT.getJWTClaimsSet().getClaim("type")).isEqualTo(TokenType.ACCESS.getValue());
        assertThat(signedJWT.getJWTClaimsSet().getJWTID()).isNotNull();
    }

    @Test
    @DisplayName("Refresh Token 생성 - 유효한 클레임 포함")
    void generateRefreshToken_ShouldContainValidClaims() throws ParseException {
        // When
        String refreshToken = jwtTokenProvider.generateRefreshToken(testUserDetails);

        // Then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken).isNotEmpty();

        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        assertThat(signedJWT.getJWTClaimsSet().getSubject()).isEqualTo("test@example.com");
        assertThat(signedJWT.getJWTClaimsSet().getClaim("userId")).isEqualTo("test-user-123");
        assertThat(signedJWT.getJWTClaimsSet().getClaim("type")).isEqualTo(TokenType.REFRESH.getValue());
    }

    @Test
    @DisplayName("Access Token 검증 - 유효한 토큰")
    void validateAccessToken_ValidToken_ShouldReturnTrue() {
        // Given
        String accessToken = jwtTokenProvider.generateAccessToken(testUserDetails);

        // When
        boolean isValid = jwtTokenProvider.validateAccessToken(accessToken);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Refresh Token 검증 - 유효한 토큰")
    void validateRefreshToken_ValidToken_ShouldReturnTrue() {
        // Given
        String refreshToken = jwtTokenProvider.generateRefreshToken(testUserDetails);

        // When
        boolean isValid = jwtTokenProvider.validateRefreshToken(refreshToken);

        // Then
        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Access Token 검증 - 잘못된 토큰 타입")
    void validateAccessToken_WrongTokenType_ShouldReturnFalse() {
        // Given - Refresh Token을 생성
        String refreshToken = jwtTokenProvider.generateRefreshToken(testUserDetails);

        // When - Access Token으로 검증
        boolean isValid = jwtTokenProvider.validateAccessToken(refreshToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Refresh Token 검증 - 잘못된 토큰 타입")
    void validateRefreshToken_WrongTokenType_ShouldReturnFalse() {
        // Given - Access Token을 생성
        String accessToken = jwtTokenProvider.generateAccessToken(testUserDetails);

        // When - Refresh Token으로 검증
        boolean isValid = jwtTokenProvider.validateRefreshToken(accessToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("토큰 검증 - 유효하지 않은 서명")
    void validateToken_InvalidSignature_ShouldReturnFalse() {
        // Given
        String invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIn0.invalid_signature";

        // When
        boolean isValid = jwtTokenProvider.validateAccessToken(invalidToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("토큰 검증 - 형식이 잘못된 토큰")
    void validateToken_MalformedToken_ShouldReturnFalse() {
        // Given
        String malformedToken = "not.a.valid.jwt.token";

        // When
        boolean isValid = jwtTokenProvider.validateAccessToken(malformedToken);

        // Then
        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("UserId 추출 - 유효한 토큰")
    void getUserId_ValidToken_ShouldReturnUserId() {
        // Given
        String accessToken = jwtTokenProvider.generateAccessToken(testUserDetails);

        // When
        String userId = jwtTokenProvider.getUserId(accessToken);

        // Then
        assertThat(userId).isEqualTo("test-user-123");
    }

    @Test
    @DisplayName("Subject(Email) 추출 - 유효한 토큰")
    void getSubject_ValidToken_ShouldReturnEmail() {
        // Given
        String accessToken = jwtTokenProvider.generateAccessToken(testUserDetails);

        // When
        String subject = jwtTokenProvider.getSubject(accessToken);

        // Then
        assertThat(subject).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Token ID 추출 - 유효한 토큰")
    void getTokenId_ValidToken_ShouldReturnJwtId() {
        // Given
        String accessToken = jwtTokenProvider.generateAccessToken(testUserDetails);

        // When
        String tokenId = jwtTokenProvider.getTokenId(accessToken);

        // Then
        assertThat(tokenId).isNotNull();
        assertThat(tokenId).isNotEmpty();
    }

    @Test
    @DisplayName("토큰 만료 시간 확인 - Access Token")
    void accessToken_ShouldHaveCorrectExpiration() throws ParseException {
        // Given
        long beforeGeneration = System.currentTimeMillis();
        String accessToken = jwtTokenProvider.generateAccessToken(testUserDetails);
        long afterGeneration = System.currentTimeMillis();

        // When
        SignedJWT signedJWT = SignedJWT.parse(accessToken);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Then
        long expectedExpiration = beforeGeneration + ACCESS_TOKEN_EXPIRATION;
        assertThat(expirationTime.getTime()).isBetween(
                expectedExpiration - 1000, // 1초 여유
                afterGeneration + ACCESS_TOKEN_EXPIRATION + 1000);
    }

    @Test
    @DisplayName("토큰 만료 시간 확인 - Refresh Token")
    void refreshToken_ShouldHaveCorrectExpiration() throws ParseException {
        // Given
        long beforeGeneration = System.currentTimeMillis();
        String refreshToken = jwtTokenProvider.generateRefreshToken(testUserDetails);
        long afterGeneration = System.currentTimeMillis();

        // When
        SignedJWT signedJWT = SignedJWT.parse(refreshToken);
        Date expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        // Then
        long expectedExpiration = beforeGeneration + REFRESH_TOKEN_EXPIRATION;
        assertThat(expirationTime.getTime()).isBetween(
                expectedExpiration - 1000, // 1초 여유
                afterGeneration + REFRESH_TOKEN_EXPIRATION + 1000);
    }

    @Test
    @DisplayName("각 토큰은 고유한 JWT ID를 가짐")
    void eachToken_ShouldHaveUniqueJwtId() {
        // Given & When
        String token1 = jwtTokenProvider.generateAccessToken(testUserDetails);
        String token2 = jwtTokenProvider.generateAccessToken(testUserDetails);

        String jwtId1 = jwtTokenProvider.getTokenId(token1);
        String jwtId2 = jwtTokenProvider.getTokenId(token2);

        // Then
        assertThat(jwtId1).isNotEqualTo(jwtId2);
    }

    @Test
    @DisplayName("Refresh Token 쿠키 생성")
    void generateRefreshTokenCookie_ShouldCreateValidCookie() {
        // Given
        String refreshToken = "test.refresh.token";

        // When
        jakarta.servlet.http.Cookie cookie = jwtTokenProvider.generateRefreshTokenCookie(refreshToken);

        // Then
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("REFRESH_TOKEN");
        assertThat(cookie.getValue()).isEqualTo(refreshToken);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Refresh Token 만료 쿠키 생성")
    void generateRefreshTokenExpirationCookie_ShouldCreateExpiredCookie() {
        // When
        jakarta.servlet.http.Cookie cookie = jwtTokenProvider.generateRefreshTokenExpirationCookie();

        // Then
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("REFRESH_TOKEN");
        assertThat(cookie.getValue()).isEmpty();
        assertThat(cookie.getMaxAge()).isEqualTo(0);
    }
}
