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
    private Long questionId;
    private String interviewId;
    private String aiQuestion;
    private String userAnswer;
    private LocalDateTime createdAt;
    private Long answerTime;
}
