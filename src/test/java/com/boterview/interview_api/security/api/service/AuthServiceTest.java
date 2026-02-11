package com.boterview.interview_api.security.api.service;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;
import com.boterview.interview_api.domain.user.entity.OAuthProvider;
import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;
import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;
import com.boterview.interview_api.security.authentication.jwt.exception.InValidRefreshTokenException;
import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;
import com.boterview.interview_api.security.authentication.jwt.registry.JwtRegistry;
import com.boterview.interview_api.security.core.dto.SecurityUserDto;
import com.boterview.interview_api.security.core.principal.BotUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.ApplicationEventPublisher;
import com.boterview.interview_api.security.event.PasswordResetEvent;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * AuthService 단위 테스트
 * 로그인, 회원가입, 토큰 관리, 비밀번호 재설정 등의 인증 기능을 테스트
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtRegistry jwtRegistry;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private AuthService authService;

    private User testUser;
    private BotUserDetails testUserDetails;
    private String testEmail = "test@example.com";
    private String testPassword = "password123";
    private String encodedPassword = "encodedPassword123";

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .userId(UUID.randomUUID().toString())
                .email(testEmail)
                .password(encodedPassword)
                .name("Test User")
                .createdAt(LocalDateTime.now())
                .build();

        SecurityUserDto userDto = SecurityUserDto.from(testUser);
        testUserDetails = new BotUserDetails(userDto, encodedPassword);
    }

    @Test
    @DisplayName("로그인 성공 - 유효한 이메일과 비밀번호")
    void login_Success_WithValidCredentials() {
        // Given
        String accessToken = "test.access.token";
        String refreshToken = "test.refresh.token";

        when(userDetailsService.loadUserByUsername(testEmail)).thenReturn(testUserDetails);
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(true);
        when(tokenProvider.generateAccessToken(testUserDetails)).thenReturn(accessToken);
        when(tokenProvider.generateRefreshToken(testUserDetails)).thenReturn(refreshToken);

        // When
        JwtInformation result = authService.login(testEmail, testPassword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(accessToken);
        assertThat(result.getRefreshToken()).isEqualTo(refreshToken);
        assertThat(result.getUserDto().getEmail()).isEqualTo(testEmail);

        verify(userDetailsService).loadUserByUsername(testEmail);
        verify(passwordEncoder).matches(testPassword, encodedPassword);
        verify(tokenProvider).generateAccessToken(testUserDetails);
        verify(tokenProvider).generateRefreshToken(testUserDetails);
        verify(jwtRegistry).registerJwtInformation(any(JwtInformation.class));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_Failure_UserNotFound() {
        // Given
        when(userDetailsService.loadUserByUsername(testEmail))
                .thenThrow(new UsernameNotFoundException("User not found"));

        // When & Then
        assertThatThrownBy(() -> authService.login(testEmail, testPassword))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIAL);

        verify(userDetailsService).loadUserByUsername(testEmail);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Failure_InvalidPassword() {
        // Given
        when(userDetailsService.loadUserByUsername(testEmail)).thenReturn(testUserDetails);
        when(passwordEncoder.matches(testPassword, encodedPassword)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(testEmail, testPassword))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIAL);

        verify(userDetailsService).loadUserByUsername(testEmail);
        verify(passwordEncoder).matches(testPassword, encodedPassword);
        verify(tokenProvider, never()).generateAccessToken(any());
    }

    @Test
    @DisplayName("회원가입 성공 - 새로운 사용자")
    void signup_Success_NewUser() {
        // Given
        String newEmail = "newuser@example.com";
        String newPassword = "newpassword123";
        String newName = "New User";

        when(userMapper.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // When
        String userId = authService.signup(newEmail, newPassword, newName);

        // Then
        assertThat(userId).isNotNull();
        assertThat(userId).isNotEmpty();

        verify(userMapper).findByEmail(newEmail);
        verify(passwordEncoder).encode(newPassword);
        verify(userMapper).insert(argThat(user -> user.getEmail().equals(newEmail) &&
                user.getName().equals(newName) &&
                user.getPassword().equals("encodedNewPassword")));
    }

    @Test
    @DisplayName("회원가입 실패 - 이미 존재하는 이메일")
    void signup_Failure_EmailAlreadyExists() {
        // Given
        when(userMapper.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When & Then
        assertThatThrownBy(() -> authService.signup(testEmail, testPassword, "Name"))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_ALREADY_EXISTS);

        verify(userMapper).findByEmail(testEmail);
        verify(userMapper, never()).insert(any());
    }

    @Test
    @DisplayName("토큰 갱신 성공 - 유효한 리프레시 토큰")
    void refreshToken_Success_ValidRefreshToken() {
        // Given
        String oldRefreshToken = "old.refresh.token";
        String newAccessToken = "new.access.token";
        String newRefreshToken = "new.refresh.token";

        when(tokenProvider.validateRefreshToken(oldRefreshToken)).thenReturn(true);
        when(jwtRegistry.hasActiveJwtInformationByRefreshToken(oldRefreshToken)).thenReturn(true);
        when(tokenProvider.getSubject(oldRefreshToken)).thenReturn(testEmail);
        when(userDetailsService.loadUserByUsername(testEmail)).thenReturn(testUserDetails);
        when(tokenProvider.generateAccessToken(testUserDetails)).thenReturn(newAccessToken);
        when(tokenProvider.generateRefreshToken(testUserDetails)).thenReturn(newRefreshToken);

        // When
        JwtInformation result = authService.refreshToken(oldRefreshToken);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getAccessToken()).isEqualTo(newAccessToken);
        assertThat(result.getRefreshToken()).isEqualTo(newRefreshToken);

        verify(tokenProvider).validateRefreshToken(oldRefreshToken);
        verify(jwtRegistry).hasActiveJwtInformationByRefreshToken(oldRefreshToken);
        verify(jwtRegistry).rotateJwtInformation(eq(oldRefreshToken), any(JwtInformation.class));
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 유효하지 않은 리프레시 토큰")
    void refreshToken_Failure_InvalidRefreshToken() {
        // Given
        String invalidRefreshToken = "invalid.refresh.token";
        when(tokenProvider.validateRefreshToken(invalidRefreshToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(invalidRefreshToken))
                .isInstanceOf(InValidRefreshTokenException.class);

        verify(tokenProvider).validateRefreshToken(invalidRefreshToken);
        verify(jwtRegistry, never()).rotateJwtInformation(anyString(), any());
    }

    @Test
    @DisplayName("토큰 갱신 실패 - 비활성화된 토큰")
    void refreshToken_Failure_InactiveToken() {
        // Given
        String inactiveToken = "inactive.refresh.token";
        when(tokenProvider.validateRefreshToken(inactiveToken)).thenReturn(true);
        when(jwtRegistry.hasActiveJwtInformationByRefreshToken(inactiveToken)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.refreshToken(inactiveToken))
                .isInstanceOf(InValidRefreshTokenException.class);

        verify(jwtRegistry).hasActiveJwtInformationByRefreshToken(inactiveToken);
        verify(jwtRegistry, never()).rotateJwtInformation(anyString(), any());
    }

    @Test
    @DisplayName("로그아웃 성공 - 유효한 리프레시 토큰")
    void logout_Success_ValidRefreshToken() {
        // Given
        String refreshToken = "valid.refresh.token";
        when(tokenProvider.validateRefreshToken(refreshToken)).thenReturn(true);
        when(tokenProvider.getSubject(refreshToken)).thenReturn(testEmail);
        when(userMapper.findByEmail(testEmail)).thenReturn(Optional.of(testUser));

        // When
        authService.logout(refreshToken);

        // Then
        verify(tokenProvider).validateRefreshToken(refreshToken);
        verify(tokenProvider).getSubject(refreshToken);
        verify(userMapper).findByEmail(testEmail);
        verify(jwtRegistry).invalidateJwtInformationByUserId(testUser.getUserId());
    }

    @Test
    @DisplayName("로그아웃 - 토큰이 null인 경우 예외 없이 처리")
    void logout_NullToken_NoException() {
        // When & Then
        assertThatCode(() -> authService.logout(null))
                .doesNotThrowAnyException();

        verify(tokenProvider, never()).validateRefreshToken(any());
    }

    @Test
    @DisplayName("로그아웃 - 유효하지 않은 토큰은 무시")
    void logout_InvalidToken_NoException() {
        // Given
        String invalidToken = "invalid.token";
        when(tokenProvider.validateRefreshToken(invalidToken)).thenReturn(false);

        // When & Then
        assertThatCode(() -> authService.logout(invalidToken))
                .doesNotThrowAnyException();

        verify(tokenProvider).validateRefreshToken(invalidToken);
        verify(jwtRegistry, never()).invalidateJwtInformationByUserId(any());
    }

    @Test
    @DisplayName("비밀번호 재설정 성공 - 일반 사용자")
    void resetPassword_Success_RegularUser() {
        // Given
        when(userMapper.findByEmail(testEmail)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

        // When
        authService.resetPassword(testEmail);

        // Then
        verify(userMapper).findByEmail(testEmail);
        verify(passwordEncoder).encode(anyString());
        verify(userMapper).updatePassword(eq(testUser.getUserId()), eq("newEncodedPassword"));
        verify(jwtRegistry).invalidateJwtInformationByUserId(testUser.getUserId());
        verify(eventPublisher).publishEvent(any(PasswordResetEvent.class));
    }

    @Test
    @DisplayName("비밀번호 재설정 실패 - 존재하지 않는 사용자")
    void resetPassword_Failure_UserNotFound() {
        // Given
        when(userMapper.findByEmail(testEmail)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> authService.resetPassword(testEmail))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.USER_NOT_FOUND);

        verify(userMapper).findByEmail(testEmail);
        verify(userMapper, never()).updatePassword(any(), any());
    }

    @Test
    @DisplayName("비밀번호 재설정 실패 - OAuth 사용자")
    void resetPassword_Failure_OAuthUser() {
        // Given
        User oauthUser = User.builder()
                .userId(UUID.randomUUID().toString())
                .email("oauth@google.com")
                .name("OAuth User")
                .oauth(OAuthProvider.GOOGLE)
                .createdAt(LocalDateTime.now())
                .build();

        when(userMapper.findByEmail("oauth@google.com")).thenReturn(Optional.of(oauthUser));

        // When & Then
        assertThatThrownBy(() -> authService.resetPassword("oauth@google.com"))
                .isInstanceOf(BaseException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.AUTH_PASSWORD_ERROR);

        verify(userMapper).findByEmail("oauth@google.com");
        verify(userMapper, never()).updatePassword(any(), any());
    }
}
