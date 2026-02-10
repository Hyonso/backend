package com.boterview.interview_api.domain.dashboard.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSettingResponseDto {

    private String settingId;
    private String userId;
    private int questionCount;
    private String interviewerStyle;
    private String interviewerGender;
    private String interviewerAppearance;
    private String job;
    private String resumeS3Path;
    private LocalDateTime createdAt;
    private List<SkillDto> skills;
    private List<PreQuestionDto> preQuestions;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillDto {
        private String skillId;
        private String name;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreQuestionDto {
        private String preQuestionId;
        private String question;
        private String answer;
    }
}
