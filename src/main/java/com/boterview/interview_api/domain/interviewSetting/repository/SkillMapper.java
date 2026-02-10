package com.boterview.interview_api.domain.interviewSetting.repository;

import com.boterview.interview_api.domain.interviewSetting.entity.Skill;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface SkillMapper {

<<<<<<< HEAD
    @Select("SELECT skill_id, skill FROM skill WHERE skill = #{skill} LIMIT 1")
    Optional<Skill> findBySkill(String skill);

    @Insert("INSERT INTO skill (skill_id, skill) VALUES (#{skillId}, #{skill})")
=======
    @Insert("INSERT IGNORE INTO skill (skill_id, skill) VALUES (#{skillId}, #{skill})")
>>>>>>> 461bf75 (add: id가 BIGINT 타입으로 되어있는것 수정)
    void insert(Skill skill);

}
