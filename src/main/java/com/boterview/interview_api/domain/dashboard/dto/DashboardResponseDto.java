package com.boterview.interview_api.domain.dashboard.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DashboardResponseDto {

    private UserDto user;
    private StatsDto status;
    private List<RecentInterviewDto> recentInterviews;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class UserDto {
        private String userId;
        private String name;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class StatsDto {
        private int totalInterviews;
        private int averageScore;
        private int totalTimeMinutes;
        private int bestScore;
    }

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RecentInterviewDto {
        private String interviewId;
        private String settingId;
        private LocalDateTime createdAt;
        private String title;
        private String job;
        private List<String> skills;
        private int durationMinutes;
        private int questionCount;
        private int score;
        private int remainDate;
    }
}
