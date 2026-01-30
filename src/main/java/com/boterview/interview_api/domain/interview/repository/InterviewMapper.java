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

}
