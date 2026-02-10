package com.boterview.interview_api.domain.interview.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewQuestion {
    private String questionId;
    private String interviewId;
    private String question;
    private String answer;
    private LocalDateTime createdAt;
    private Long elapsedTime;
}
