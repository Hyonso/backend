package com.boterview.interview_api.domain.interview.repository;

import com.boterview.interview_api.domain.interview.entity.InterviewQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InterviewQuestionMapper {


    @Insert("INSERT INTO interview_question (interview_id, ai_question, user_answer, created_at, answer_time) " +
            "VALUES (#{interviewId}, #{aiQuestion}, #{userAnswer}, #{createdAt}, #{answerTime})")
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    void insert(InterviewQuestion question);

}
