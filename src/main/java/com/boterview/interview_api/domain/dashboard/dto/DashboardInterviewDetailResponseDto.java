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
public class DashboardInterviewDetailResponseDto {

    private String interviewId;
    private String settingId;
    private Long durationMs;
    private LocalDateTime createdAt;
    private String aiOverallSummary;
    private List<QuestionDto> questions;
    private List<ScoreDto> scores;
    private CountsDto counts;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionDto {
        private String questionId;
        private String aiQuestion;
        private String userAnswer;
        private LocalDateTime createdAt;
        private Long elapsedMs;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScoreDto {
        private String scoreId;
        private String type;
        private Integer score;
        private String aiEvaluation;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CountsDto {
        private int questionCount;
        private int scoreCount;
    }
}
