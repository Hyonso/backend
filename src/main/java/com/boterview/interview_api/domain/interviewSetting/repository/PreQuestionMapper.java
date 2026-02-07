package com.boterview.interview_api.domain.interviewSetting.repository;

import com.boterview.interview_api.domain.interviewSetting.entity.PreQuestion;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PreQuestionMapper {

    @Insert("INSERT INTO pre_question (pre_question_id, setting_id, question, answer) " +
            "VALUES (#{preQuestionId}, #{settingId}, #{question}, #{answer})")
    void insert(PreQuestion preQuestion);

}
