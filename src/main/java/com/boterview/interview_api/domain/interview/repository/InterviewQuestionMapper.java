package com.boterview.interview_api.domain.interview.repository;

import com.boterview.interview_api.domain.interview.entity.InterviewQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InterviewQuestionMapper {


    @Insert("INSERT INTO interview_question (interview_id, question, answer, created_at, elapsed_time) " +
            "VALUES (#{interviewId}, #{question}, #{answer}, #{createdAt}, #{elapsedTime})")
    @Options(useGeneratedKeys = true, keyProperty = "questionId")
    void insert(InterviewQuestion question);

    @Select("SELECT question_id, interview_id, question, answer, created_at, elapsed_time " +
            "FROM interview_question WHERE interview_id = #{interviewId}")
    List<InterviewQuestion> findByInterviewId(@Param("interviewId") String interviewId);

}
