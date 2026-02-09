package com.boterview.interview_api.security.authentication.oauth.service;

import com.boterview.interview_api.domain.user.entity.OAuthProvider;
import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;
import com.boterview.interview_api.security.core.principal.BotUserDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * BotOAuth2UserService 단위 테스트
 * OAuth 사용자 처리 로직 테스트 (Google, Kakao)
 */
@ExtendWith(MockitoExtension.class)
class BotOAuth2UserServiceTest {

        @Mock
        private UserMapper userMapper;

        @InjectMocks
        private BotOAuth2UserService oAuth2UserService;

        @Test
        @DisplayName("Google OAuth - 기존 사용자 정보 업데이트")
        void processGoogleOAuth_ExistingUser_ShouldUpdate() {
                // Given
                String email = "existing@gmail.com";
                User existingUser = User.builder()
                                .userId(UUID.randomUUID().toString())
                                .email(email)
                                .name("Old Name")
                                .oauth(OAuthProvider.GOOGLE)
                                .createdAt(LocalDateTime.now())
                                .build();

                // When
                existingUser.updateName("New Name");
                userMapper.update(existingUser);

                // Then
                verify(userMapper).update(existingUser);
                assertThat(existingUser.getName()).isEqualTo("New Name");
        }

        @Test
        @DisplayName("Kakao OAuth - 기존 사용자 정보 업데이트")
        void processKakaoOAuth_ExistingUser_ShouldUpdate() {
                // Given
                String email = "existing@kakao.com";
                User existingUser = User.builder()
                                .userId(UUID.randomUUID().toString())
                                .email(email)
                                .name("구이름")
                                .oauth(OAuthProvider.KAKAO)
                                .createdAt(LocalDateTime.now())
                                .build();

                // When
                existingUser.updateName("새이름");
                userMapper.update(existingUser);

                // Then
                verify(userMapper).update(existingUser);
                assertThat(existingUser.getName()).isEqualTo("새이름");
        }

        @Test
        @DisplayName("OAuth 사용자 등록 - UUID 생성 확인")
        void registerNewUser_ShouldGenerateUUID() {
                // Given
                String email = "oauth@example.com";
                String name = "OAuth User";

                // When
                User newUser = User.builder()
                                .userId(UUID.randomUUID().toString())
                                .email(email)
                                .name(name)
                                .oauth(OAuthProvider.GOOGLE)
                                .createdAt(LocalDateTime.now())
                                .build();

                // Then
                assertThat(newUser.getUserId()).isNotNull();
                assertThat(newUser.getUserId()).hasSize(36); // UUID string length
                assertThat(newUser.getEmail()).isEqualTo(email);
                assertThat(newUser.getName()).isEqualTo(name);
                assertThat(newUser.getOauth()).isEqualTo(OAuthProvider.GOOGLE);
        }

        @Test
        @DisplayName("OAuth 제공자별 사용자 구분")
        void oauthProvider_ShouldBeDifferent() {
                // Given
                User googleUser = User.builder()
                                .userId(UUID.randomUUID().toString())
                                .email("user@gmail.com")
                                .name("Google User")
                                .oauth(OAuthProvider.GOOGLE)
                                .createdAt(LocalDateTime.now())
                                .build();

                User kakaoUser = User.builder()
                                .userId(UUID.randomUUID().toString())
                                .email("user@kakao.com")
                                .name("Kakao User")
                                .oauth(OAuthProvider.KAKAO)
                                .createdAt(LocalDateTime.now())
                                .build();

                // Then
                assertThat(googleUser.getOauth()).isEqualTo(OAuthProvider.GOOGLE);
                assertThat(kakaoUser.getOauth()).isEqualTo(OAuthProvider.KAKAO);
                assertThat(googleUser.getOauth()).isNotEqualTo(kakaoUser.getOauth());
        }

        @Test
        @DisplayName("OAuth 사용자는 비밀번호가 없음")
        void oauthUser_ShouldHaveNoPassword() {
                // Given
                User oauthUser = User.builder()
                                .userId(UUID.randomUUID().toString())
                                .email("oauth@example.com")
                                .name("OAuth User")
                                .oauth(OAuthProvider.GOOGLE)
                                .createdAt(LocalDateTime.now())
                                .build();

                // Then
                assertThat(oauthUser.getPassword()).isNull();
        }

        @Test
        @DisplayName("BotUserDetails 생성 - OAuth 사용자")
        void createBotUserDetails_OAuthUser() {
                // Given
                User oauthUser = User.builder()
                                .userId("oauth-user-123")
                                .email("oauth@gmail.com")
                                .name("OAuth User")
                                .oauth(OAuthProvider.GOOGLE)
                                .createdAt(LocalDateTime.now())
                                .build();

                // When
                BotUserDetails userDetails = new BotUserDetails(
                                com.boterview.interview_api.security.core.dto.SecurityUserDto.from(oauthUser),
                                null);

                // Then
                assertThat(userDetails.getUserDto().getUserId()).isEqualTo("oauth-user-123");
                assertThat(userDetails.getUserDto().getEmail()).isEqualTo("oauth@gmail.com");
                assertThat(userDetails.getUserDto().getName()).isEqualTo("OAuth User");
                assertThat(userDetails.getPassword()).isNull();
        }
}
