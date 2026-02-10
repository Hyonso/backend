package com.boterview.interview_api.domain.interview.repository;

import com.boterview.interview_api.domain.interview.entity.Interview;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InterviewMapper {


    @Insert("INSERT INTO interview (interview_id, setting_id, duration, created_at, ai_overall_review, interview_name) " +
            "VALUES (#{interviewId}, #{settingId}, #{duration}, #{createdAt}, #{aiOverallReview}, #{interviewName})")
    void insert(Interview interview);

    @Select("SELECT i.interview_id, i.setting_id, i.duration, i.created_at, i.ai_overall_review, i.interview_name, " +
            "s.user_id " +
            "FROM interview i " +
            "JOIN interview_setting s ON i.setting_id = s.setting_id " +
            "WHERE i.interview_id = #{interviewId}")
    @Results({
            @Result(column = "interview_id", property = "interviewId"),
            @Result(column = "setting_id", property = "settingId"),
            @Result(column = "duration", property = "duration"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "ai_overall_review", property = "aiOverallReview"),
            @Result(column = "interview_name", property = "interviewName")
    })
    Optional<Interview> findById(@Param("interviewId") String interviewId);

    @Select("SELECT s.user_id FROM interview i " +
            "JOIN interview_setting s ON i.setting_id = s.setting_id " +
            "WHERE i.interview_id = #{interviewId}")
    Optional<String> findUserIdByInterviewId(@Param("interviewId") String interviewId);

}
