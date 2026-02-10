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
public class InterviewSettingRequestDto {

	private TargetPosition targetPosition;
	private List<String> skills;
	private InterviewerStyle personalityType;
	private InterviewerAppearance appearanceStyle;
	private InterviewerGender gender;
	private int questionCount;
	private List<PreQuestion> preQuestions;

	@Data
	@Builder
	@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
	public static class PreQuestion {
		private String question;
		private String answer;
	}
}
