package com.boterview.interview_api.integration;

import com.boterview.interview_api.domain.interview.entity.Interview;
import com.boterview.interview_api.domain.interview.repository.InterviewMapper;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewSetting;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerAppearance;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerGender;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerStyle;
import com.boterview.interview_api.domain.interviewSetting.repository.InterviewSettingMapper;
import com.boterview.interview_api.domain.user.entity.OAuthProvider;
import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MyBatis 통합 테스트
 * 전체 도메인 간 연관 관계 및 트랜잭션 테스트
 */
@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class MyBatisIntegrationTest {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private InterviewSettingMapper interviewSettingMapper;

    @Autowired
    private InterviewMapper interviewMapper;

    @Test
    @DisplayName("통합 테스트 - 사용자 생성부터 면접 완료까지 전체 플로우")
    void testCompleteInterviewFlow() throws Exception {
        // 1. 사용자 생성
        String userId = UUID.randomUUID().toString();
        User newUser = User.builder()
                .userId(userId)
                .email("integrationtest@example.com")
                .password("hashedPassword")
                .name("통합테스트유저")
                .createdAt(LocalDateTime.now())
                .oauth(null)
                .build();
        userMapper.insert(newUser);

        // 2. 사용자 조회 확인
        Optional<User> foundUser = userMapper.findByEmail("integrationtest@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("통합테스트유저");

        // 3. 면접 설정 생성
        String settingId = UUID.randomUUID().toString();
        InterviewSetting setting = InterviewSetting.builder()
                .settingId(settingId)
                .userId(userId)
                .questionCount(10)
                .interviewerStyle(InterviewerStyle.FORMAL)
                .interviewerGender(InterviewerGender.MALE)
                .interviewerAppearance(InterviewerAppearance.REAL)
                .createdAt(LocalDateTime.now())
                .resumeUri("https://example.com/resume.pdf")
                .position("백엔드 개발자")
                .build();
        interviewSettingMapper.insert(setting);

        // 4. 면접 시작
        String interviewId = UUID.randomUUID().toString();
        Interview interview = createInterview(
                interviewId,
                settingId,
                null,
                LocalDateTime.now(),
                null,
                "Spring Boot 백엔드 면접");
        interviewMapper.insert(interview);

        // 5. 검증
        assertThat(interview.getInterviewId()).isNotNull();
        assertThat(interview.getSettingId()).isEqualTo(settingId);
    }

    @Test
    @DisplayName("통합 테스트 - 여러 사용자의 면접 설정")
    void testMultipleUsersAndSettings() throws Exception {
        // Given: 두 명의 사용자 생성
        String user1Id = UUID.randomUUID().toString();
        String user2Id = UUID.randomUUID().toString();

        User user1 = User.builder()
                .userId(user1Id)
                .email("user1@test.com")
                .password("pass1")
                .name("유저1")
                .createdAt(LocalDateTime.now())
                .build();

        User user2 = User.builder()
                .userId(user2Id)
                .email("user2@test.com")
                .password("pass2")
                .name("유저2")
                .createdAt(LocalDateTime.now())
                .oauth(OAuthProvider.GOOGLE)
                .build();

        userMapper.insert(user1);
        userMapper.insert(user2);

        // When: 각 사용자의 면접 설정 생성
        InterviewSetting setting1 = InterviewSetting.builder()
                .settingId(UUID.randomUUID().toString())
                .userId(user1Id)
                .questionCount(5)
                .interviewerStyle(InterviewerStyle.CASUAL)
                .interviewerGender(InterviewerGender.FEMALE)
                .interviewerAppearance(InterviewerAppearance.ANIME)
                .createdAt(LocalDateTime.now())
                .position("프론트엔드")
                .build();

        InterviewSetting setting2 = InterviewSetting.builder()
                .settingId(UUID.randomUUID().toString())
                .userId(user2Id)
                .questionCount(15)
                .interviewerStyle(InterviewerStyle.PRESSURE)
                .interviewerGender(InterviewerGender.MALE)
                .interviewerAppearance(InterviewerAppearance.REAL)
                .createdAt(LocalDateTime.now())
                .position("DevOps")
                .build();

        interviewSettingMapper.insert(setting1);
        interviewSettingMapper.insert(setting2);

        // Then: 모든 데이터 정상 삽입 확인
        Optional<User> foundUser1 = userMapper.findByEmail("user1@test.com");
        Optional<User> foundUser2 = userMapper.findByEmail("user2@test.com");

        assertThat(foundUser1).isPresent();
        assertThat(foundUser2).isPresent();
        assertThat(foundUser1.get().getOauth()).isNull();
        assertThat(foundUser2.get().getOauth()).isEqualTo(OAuthProvider.GOOGLE);
    }

    @Test
    @DisplayName("통합 테스트 - 같은 사용자의 여러 면접")
    void testMultipleInterviewsForSameUser() throws Exception {
        // Given: 사용자 및 설정
        String userId = "test-user-1";
        String settingId = UUID.randomUUID().toString();

        InterviewSetting setting = InterviewSetting.builder()
                .settingId(settingId)
                .userId(userId)
                .questionCount(10)
                .interviewerStyle(InterviewerStyle.FORMAL)
                .interviewerGender(InterviewerGender.MALE)
                .interviewerAppearance(InterviewerAppearance.REAL)
                .createdAt(LocalDateTime.now())
                .position("풀스택")
                .build();
        interviewSettingMapper.insert(setting);

        // When: 같은 설정으로 3번의 면접 진행
        Interview interview1 = createInterview(
                UUID.randomUUID().toString(),
                settingId,
                1200L,
                LocalDateTime.now().minusDays(3),
                "첫 번째 면접 완료",
                "1차 면접");

        Interview interview2 = createInterview(
                UUID.randomUUID().toString(),
                settingId,
                1500L,
                LocalDateTime.now().minusDays(1),
                "두 번째 면접 완료",
                "2차 면접");

        Interview interview3 = createInterview(
                UUID.randomUUID().toString(),
                settingId,
                null,
                LocalDateTime.now(),
                null,
                "진행 중인 면접");

        interviewMapper.insert(interview1);
        interviewMapper.insert(interview2);
        interviewMapper.insert(interview3);

        // Then: 모든 면접이 정상적으로 삽입됨
        assertThat(interview1.getInterviewId()).isNotEqualTo(interview2.getInterviewId());
        assertThat(interview2.getInterviewId()).isNotEqualTo(interview3.getInterviewId());
    }

    /**
     * Interview 객체 생성 헬퍼 메서드 (Reflection 사용)
     */
    private Interview createInterview(
            String interviewId,
            String settingId,
            Long duration,
            LocalDateTime createdAt,
            String aiOverallReview,
            String interviewName) throws Exception {
        Interview interview = new Interview();
        setField(interview, "interviewId", interviewId);
        setField(interview, "settingId", settingId);
        setField(interview, "duration", duration);
        setField(interview, "createdAt", createdAt);
        setField(interview, "aiOverallReview", aiOverallReview);
        setField(interview, "interviewName", interviewName);
        return interview;
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}
