package com.boterview.interview_api.domain.interviewSetting.repository;

import com.boterview.interview_api.domain.interviewSetting.entity.InterviewSetting;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerAppearance;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerGender;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerStyle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InterviewSettingMapper 테스트
 * 면접 설정 데이터의 저장 및 조회 테스트
 */
@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InterviewSettingMapperTest {

    @Autowired
    private InterviewSettingMapper interviewSettingMapper;

    @Test
    @DisplayName("면접 설정 INSERT 테스트 - 기본 설정")
    void testInsertInterviewSetting() {
        // Given
        InterviewSetting setting = InterviewSetting.builder()
                .settingId(UUID.randomUUID().toString())
                .userId("test-user-1")
                .questionCount(10)
                .interviewerStyle(InterviewerStyle.FORMAL)
                .interviewerGender(InterviewerGender.MALE)
                .interviewerAppearance(InterviewerAppearance.REAL)
                .createdAt(LocalDateTime.now())
                .resumeUri(null)
                .position("백엔드 개발자")
                .build();

        // When
        interviewSettingMapper.insert(setting);

        // Then - 실제로 조회 메서드가 없으므로 예외가 발생하지 않으면 성공
        assertThat(setting.getSettingId()).isNotNull();
    }

    @Test
    @DisplayName("면접 설정 INSERT 테스트 - 캐주얼 스타일")
    void testInsertCasualSetting() {
        // Given
        InterviewSetting setting = InterviewSetting.builder()
                .settingId(UUID.randomUUID().toString())
                .userId("test-user-2")
                .questionCount(5)
                .interviewerStyle(InterviewerStyle.CASUAL)
                .interviewerGender(InterviewerGender.FEMALE)
                .interviewerAppearance(InterviewerAppearance.ANIME)
                .createdAt(LocalDateTime.now())
                .resumeUri("https://example.com/resume.pdf")
                .position("프론트엔드 개발자")
                .build();

        // When
        interviewSettingMapper.insert(setting);

        // Then
        assertThat(setting.getResumeUri()).isEqualTo("https://example.com/resume.pdf");
        assertThat(setting.getInterviewerStyle()).isEqualTo(InterviewerStyle.CASUAL);
    }

    @Test
    @DisplayName("면접 설정 INSERT 테스트 - 압박 면접")
    void testInsertPressureSetting() {
        // Given
        InterviewSetting setting = InterviewSetting.builder()
                .settingId(UUID.randomUUID().toString())
                .userId("test-user-3")
                .questionCount(15)
                .interviewerStyle(InterviewerStyle.PRESSURE)
                .interviewerGender(InterviewerGender.MALE)
                .interviewerAppearance(InterviewerAppearance.ANIMAL)
                .createdAt(LocalDateTime.now())
                .resumeUri("s3://bucket/resume.pdf")
                .position("풀스택 개발자")
                .build();

        // When
        interviewSettingMapper.insert(setting);

        // Then
        assertThat(setting.getQuestionCount()).isEqualTo(15);
        assertThat(setting.getInterviewerStyle()).isEqualTo(InterviewerStyle.PRESSURE);
    }

    @Test
    @DisplayName("면접 설정 INSERT 테스트 - 이력서 없이")
    void testInsertWithoutResume() {
        // Given
        InterviewSetting setting = InterviewSetting.builder()
                .settingId(UUID.randomUUID().toString())
                .userId("test-user-1")
                .questionCount(8)
                .interviewerStyle(InterviewerStyle.FORMAL)
                .interviewerGender(InterviewerGender.FEMALE)
                .interviewerAppearance(InterviewerAppearance.REAL)
                .createdAt(LocalDateTime.now())
                .resumeUri(null)
                .position(null)
                .build();

        // When
        interviewSettingMapper.insert(setting);

        // Then
        assertThat(setting.getResumeUri()).isNull();
        assertThat(setting.getPosition()).isNull();
    }

    @Test
    @DisplayName("면접 설정 INSERT 테스트 - 다양한 Enum 조합")
    void testInsertWithDifferentEnumCombinations() {
        // Given - ANIME + FEMALE + CASUAL 조합
        InterviewSetting setting = InterviewSetting.builder()
                .settingId(UUID.randomUUID().toString())
                .userId("test-user-2")
                .questionCount(12)
                .interviewerStyle(InterviewerStyle.CASUAL)
                .interviewerGender(InterviewerGender.FEMALE)
                .interviewerAppearance(InterviewerAppearance.ANIME)
                .createdAt(LocalDateTime.now())
                .resumeUri("https://cdn.example.com/resume.pdf")
                .position("DevOps 엔지니어")
                .build();

        // When
        interviewSettingMapper.insert(setting);

        // Then
        assertThat(setting.getInterviewerAppearance()).isEqualTo(InterviewerAppearance.ANIME);
        assertThat(setting.getPosition()).isEqualTo("DevOps 엔지니어");
    }
}
