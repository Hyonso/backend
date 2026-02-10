package com.boterview.interview_api.domain.interview.repository;

import com.boterview.interview_api.domain.interview.entity.Interview;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewSetting;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerAppearance;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerGender;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerStyle;
import com.boterview.interview_api.domain.interviewSetting.repository.InterviewSettingMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InterviewMapper 테스트
 * 면접 진행 데이터의 저장 및 조회 테스트
 */
@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(scripts = "/test-data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class InterviewMapperTest {

    @Autowired
    private InterviewMapper interviewMapper;

    @Autowired
    private InterviewSettingMapper interviewSettingMapper;

    private String testSettingId;

    @BeforeEach
    void setUp() {
        // 면접을 생성하려면 먼저 설정이 필요함
        testSettingId = UUID.randomUUID().toString();
        InterviewSetting setting = InterviewSetting.builder()
                .settingId(testSettingId)
                .userId("test-user-1")
                .questionCount(10)
                .interviewerStyle(InterviewerStyle.FORMAL)
                .interviewerGender(InterviewerGender.MALE)
                .interviewerAppearance(InterviewerAppearance.REAL)
                .createdAt(LocalDateTime.now())
                .position("백엔드 개발자")
                .build();
        interviewSettingMapper.insert(setting);
    }

    @Test
    @DisplayName("면접 INSERT 테스트 - 기본 면접 생성")
    void testInsertInterview() throws Exception {
        // Given
        String interviewId = UUID.randomUUID().toString();
        Interview interview = createInterview(
                interviewId,
                testSettingId,
                null,
                null,
                null,
                "Java 백엔드 모의면접");

        // When
        interviewMapper.insert(interview);

        // Then
        assertThat(interview.getInterviewId()).isEqualTo(interviewId);
        assertThat(interview.getSettingId()).isEqualTo(testSettingId);
    }

    @Test
    @DisplayName("면접 INSERT 테스트 - 완료된 면접")
    void testInsertCompletedInterview() throws Exception {
        // Given
        Interview interview = createInterview(
                UUID.randomUUID().toString(),
                testSettingId,
                1800L, // 30분 (초 단위)
                LocalDateTime.now(),
                "전반적으로 우수한 답변이었습니다. 특히 Spring Security에 대한 이해도가 높았습니다.",
                "Spring Security 심화 면접");

        // When
        interviewMapper.insert(interview);

        // Then
        assertThat(interview.getDuration()).isEqualTo(1800L);
        assertThat(interview.getAiOverallReview()).contains("우수한 답변");
    }

    @Test
    @DisplayName("면접 INSERT 테스트 - 진행 중인 면접")
    void testInsertOngoingInterview() throws Exception {
        // Given
        Interview interview = createInterview(
                UUID.randomUUID().toString(),
                testSettingId,
                null,
                LocalDateTime.now(),
                null,
                "데이터베이스 튜닝 면접");

        // When
        interviewMapper.insert(interview);

        // Then
        assertThat(interview.getDuration()).isNull();
        assertThat(interview.getAiOverallReview()).isNull();
    }

    @Test
    @DisplayName("면접 INSERT 테스트 - 장시간 면접")
    void testInsertLongInterview() throws Exception {
        // Given
        Interview interview = createInterview(
                UUID.randomUUID().toString(),
                testSettingId,
                7200L, // 2시간
                LocalDateTime.now(),
                "깊이 있는 토론이 이루어졌습니다. 시스템 설계 역량이 뛰어납니다.",
                "시스템 설계 심층 면접");

        // When
        interviewMapper.insert(interview);

        // Then
        assertThat(interview.getDuration()).isEqualTo(7200L);
        assertThat(interview.getInterviewName()).isEqualTo("시스템 설계 심층 면접");
    }

    @Test
    @DisplayName("면접 INSERT 테스트 - 짧은 면접")
    void testInsertShortInterview() throws Exception {
        // Given
        Interview interview = createInterview(
                UUID.randomUUID().toString(),
                testSettingId,
                300L, // 5분
                LocalDateTime.now(),
                "간단한 기술 스크리닝이 완료되었습니다.",
                "빠른 기술 체크");

        // When
        interviewMapper.insert(interview);

        // Then
        assertThat(interview.getDuration()).isEqualTo(300L);
    }

    @Test
    @DisplayName("면접 INSERT 테스트 - 여러 면접 연속 생성")
    void testInsertMultipleInterviews() throws Exception {
        // Given
        Interview interview1 = createInterview(
                UUID.randomUUID().toString(),
                testSettingId,
                1000L,
                LocalDateTime.now(),
                "첫 번째 면접 리뷰",
                "면접 1차");

        Interview interview2 = createInterview(
                UUID.randomUUID().toString(),
                testSettingId,
                2000L,
                LocalDateTime.now(),
                "두 번째 면접 리뷰",
                "면접 2차");

        // When
        interviewMapper.insert(interview1);
        interviewMapper.insert(interview2);

        // Then
        assertThat(interview1.getInterviewId()).isNotEqualTo(interview2.getInterviewId());
        assertThat(interview1.getSettingId()).isEqualTo(interview2.getSettingId());
    }

    /**
     * Interview 객체 생성 헬퍼 메서드 (Reflection 사용)
     * Interview 클래스에 Builder가 없으므로 Reflection으로 필드 설정
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
