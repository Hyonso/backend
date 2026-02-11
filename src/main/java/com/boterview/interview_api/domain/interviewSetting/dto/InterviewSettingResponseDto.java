package com.boterview.interview_api.domain.interviewSetting.dto;

import java.util.List;

import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerAppearance;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerGender;
import com.boterview.interview_api.domain.interviewSetting.entity.InterviewerStyle;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class InterviewSettingResponseDto {

	private String settingId;
}
