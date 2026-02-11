package com.boterview.interview_api.domain.interviewSetting.repository;

import com.boterview.interview_api.domain.interviewSetting.entity.Skill;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SkillMapper {

    @Select("SELECT skill_id, skill FROM skill WHERE skill = #{skill} LIMIT 1")
    Optional<Skill> findBySkill(String skill);

    @Insert("INSERT INTO skill (skill_id, skill) VALUES (#{skillId}, #{skill})")
    void insert(Skill skill);

}
