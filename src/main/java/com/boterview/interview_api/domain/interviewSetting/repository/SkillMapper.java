package com.boterview.interview_api.domain.interviewSetting.repository;

import com.boterview.interview_api.domain.interviewSetting.entity.Skill;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SkillMapper {

    @Insert("INSERT IGNORE INTO skill (skill_id, skill) VALUES (#{skillId}, #{skill})")
    void insert(Skill skill);

}
