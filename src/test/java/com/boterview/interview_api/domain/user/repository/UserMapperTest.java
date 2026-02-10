package com.boterview.interview_api.domain.user.repository;

import com.boterview.interview_api.domain.user.entity.OAuthProvider;
import com.boterview.interview_api.domain.user.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UserMapper 테스트
 * H2 인메모리 데이터베이스를 사용한 MyBatis Mapper 단위 테스트
 */
@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, config = @SqlConfig(encoding = "UTF-8"))
class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    @DisplayName("사용자 INSERT 테스트 - 이메일/비밀번호 회원가입")
    void testInsertUser() {
        // Given
        User newUser = User.builder()
                .userId(UUID.randomUUID().toString())
                .email("newuser@example.com")
                .password("hashedPassword123")
                .name("신규유저")
                .createdAt(LocalDateTime.now())
                .oauth(null)
                .build();

        // When
        userMapper.insert(newUser);

        // Then
        Optional<User> foundUser = userMapper.findByEmail("newuser@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("newuser@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("신규유저");
        assertThat(foundUser.get().getPassword()).isEqualTo("hashedPassword123");
        assertThat(foundUser.get().getOauth()).isNull();
    }

    @Test
    @DisplayName("사용자 INSERT 테스트 - OAuth 소셜 로그인")
    void testInsertUserWithOAuth() {
        // Given
        User oauthUser = User.builder()
                .userId(UUID.randomUUID().toString())
                .email("kakao@kakao.com")
                .password("")
                .name("카카오유저")
                .createdAt(LocalDateTime.now())
                .oauth(OAuthProvider.KAKAO)
                .build();

        // When
        userMapper.insert(oauthUser);

        // Then
        Optional<User> foundUser = userMapper.findByEmail("kakao@kakao.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getOauth()).isEqualTo(OAuthProvider.KAKAO);
        assertThat(foundUser.get().getPassword()).isEmpty();
    }

    @Test
    @DisplayName("사용자 SELECT 테스트 - 이메일로 조회")
    void testFindByEmail() {
        // Given: test-data.sql에서 삽입된 데이터 사용
        String email = "test1@example.com";

        // When
        Optional<User> foundUser = userMapper.findByEmail(email);

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("test1@example.com");
        assertThat(foundUser.get().getName()).isEqualTo("테스트유저1");
        assertThat(foundUser.get().getOauth()).isNull();
    }

    @Test
    @DisplayName("사용자 SELECT 테스트 - 존재하지 않는 이메일")
    void testFindByEmailNotFound() {
        // Given
        String nonExistentEmail = "nonexistent@example.com";

        // When
        Optional<User> foundUser = userMapper.findByEmail(nonExistentEmail);

        // Then
        assertThat(foundUser).isEmpty();
    }

    @Test
    @DisplayName("사용자 UPDATE 테스트 - 전체 정보 수정")
    void testUpdateUser() {
        // Given: 기존 사용자 조회
        Optional<User> existingUser = userMapper.findByEmail("test1@example.com");
        assertThat(existingUser).isPresent();

        User userToUpdate = User.builder()
                .userId(existingUser.get().getUserId())
                .email("updated@example.com")
                .password("newHashedPassword")
                .name("변경된이름")
                .oauth(null)
                .build();

        // When
        userMapper.update(userToUpdate);

        // Then
        Optional<User> updatedUser = userMapper.findByEmail("updated@example.com");
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getName()).isEqualTo("변경된이름");
        assertThat(updatedUser.get().getPassword()).isEqualTo("newHashedPassword");

        // 기존 이메일로는 조회되지 않아야 함
        Optional<User> oldEmailUser = userMapper.findByEmail("test1@example.com");
        assertThat(oldEmailUser).isEmpty();
    }

    @Test
    @DisplayName("사용자 UPDATE 테스트 - 비밀번호만 변경")
    void testUpdatePassword() {
        // Given
        Optional<User> existingUser = userMapper.findByEmail("test1@example.com");
        assertThat(existingUser).isPresent();

        String userId = existingUser.get().getUserId();
        String newPassword = "superSecureNewPassword";

        // When
        userMapper.updatePassword(userId, newPassword);

        // Then
        Optional<User> updatedUser = userMapper.findByEmail("test1@example.com");
        assertThat(updatedUser).isPresent();
        assertThat(updatedUser.get().getPassword()).isEqualTo(newPassword);
        // 다른 필드는 변경되지 않아야 함
        assertThat(updatedUser.get().getName()).isEqualTo("테스트유저1");
        assertThat(updatedUser.get().getEmail()).isEqualTo("test1@example.com");
    }

    @Test
    @DisplayName("OAuth 사용자 조회 테스트")
    void testFindOAuthUser() {
        // Given
        String oauthEmail = "oauth@google.com";

        // When
        Optional<User> oauthUser = userMapper.findByEmail(oauthEmail);

        // Then
        assertThat(oauthUser).isPresent();
        assertThat(oauthUser.get().getOauth()).isEqualTo(OAuthProvider.GOOGLE);
        assertThat(oauthUser.get().getName()).isEqualTo("구글유저");
    }

    @Test
    @DisplayName("트랜잭션 테스트 - 데이터 격리 확인")
    void testDataIsolation() {
        // Given
        User tempUser = User.builder()
                .userId(UUID.randomUUID().toString())
                .email("temp@example.com")
                .password("temp")
                .name("임시유저")
                .createdAt(LocalDateTime.now())
                .build();

        // When
        userMapper.insert(tempUser);

        // Then
        Optional<User> foundUser = userMapper.findByEmail("temp@example.com");
        assertThat(foundUser).isPresent();

        // 테스트 종료 후 자동으로 롤백되어 다른 테스트에 영향 없음
    }
}
