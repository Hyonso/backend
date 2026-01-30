package com.boterview.interview_api.domain.interviewSetting.repository;

import com.boterview.interview_api.domain.interviewSetting.entity.SettingSkill;
import com.boterview.interview_api.domain.interviewSetting.entity.Skill;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SettingSkillMapper {

    @Insert("INSERT INTO setting_skill (setting_id, skill_id) VALUES (#{settingId}, #{skillId})")
    void insert(SettingSkill settingSkill);

}
