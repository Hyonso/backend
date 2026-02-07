package com.boterview.interview_api.domain.interview.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Interview {
    private String interviewId;
    private String settingId;
    private Long duration;
    private LocalDateTime createdAt;
    private String aiOverallReview;
    private String interviewName;
}
