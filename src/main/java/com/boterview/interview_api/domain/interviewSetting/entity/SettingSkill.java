package com.boterview.interview_api.domain.interviewSetting.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SettingSkill {
    private String settingId;
    private Long skillId;
}
