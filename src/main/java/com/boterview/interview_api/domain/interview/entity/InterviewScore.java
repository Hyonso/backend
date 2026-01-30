package com.boterview.interview_api.domain.interview.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class InterviewScore {
    private String scoreId;
    private String interviewId;
    private ScoreType scoreType;
    private Integer score;
    private String aiReview;
}
