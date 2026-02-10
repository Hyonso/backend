package com.boterview.interview_api.domain.interview.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InterviewResultResponseDto {

    private String interviewId;
    private String userId;
    private Long progressTime;
    private String feedback;
    private LocalDateTime createdAt;
    private List<QuestionDto> interviewQuestion;
    private List<ScoreDto> scores;

    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class QuestionDto {
        private String questionId;
        private String question;
        private String answer;
        private Long intervalTime;
    }

    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class ScoreDto {
        private String scoreId;
        private String scoreType;
        private Integer score;
        private String feedback;
    }
}
