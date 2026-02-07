package com.boterview.interview_api.domain.interviewSetting.repository;

import com.boterview.interview_api.domain.interviewSetting.entity.Skill;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SkillMapper {


    @Insert("INSERT INTO skill (skill) VALUES (#{skill})")
    @Options(useGeneratedKeys = true, keyProperty = "skillId")
    void insert(Skill skill);

}
