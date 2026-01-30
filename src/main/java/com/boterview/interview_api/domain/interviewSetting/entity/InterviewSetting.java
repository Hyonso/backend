package com.boterview.interview_api.domain.interviewSetting.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InterviewSetting {
    private String settingId;
    private String userId;
    private Integer questionCount;
    private InterviewerTone interviewerTone;
    private InterviewerGender interviewerGender;
    private InterviewerAppearance interviewerAppearance;
    private LocalDateTime createdAt;
    private String resumeS3Path;
    private JobType jobType;
}
