package com.boterview.interview_api.domain.interview.repository;

import com.boterview.interview_api.domain.interview.entity.InterviewScore;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InterviewResultMapper {


    @Insert("INSERT INTO interview_score (score_id, interview_id, score_type, score, evaludation) " +
            "VALUES (#{scoreId}, #{interviewId}, #{scoreType}, #{score}, #{evaludation})")
    void insert(InterviewScore interviewScore);

}
