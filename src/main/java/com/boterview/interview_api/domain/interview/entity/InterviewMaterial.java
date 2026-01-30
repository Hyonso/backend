package com.boterview.interview_api.domain.interview.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class InterviewMaterial {
    private String materialId;
    private String interviewId;
    private MaterialType materialType;
    private String s3Uri;
    private LocalDateTime createdAt;
}
