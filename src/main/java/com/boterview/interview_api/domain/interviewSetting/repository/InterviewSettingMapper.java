package com.boterview.interview_api.domain.interviewSetting.repository;

import com.boterview.interview_api.domain.interviewSetting.entity.InterviewSetting;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InterviewSettingMapper {


    @Insert("INSERT INTO interview_setting (setting_id, user_id, question_count, interviewer_style, " +
            "interviewer_gender, interviewer_appearance, created_at, resume_uri, position) " +
            "VALUES (#{settingId}, #{userId}, #{questionCount}, #{interviewerStyle}, " +
            "#{interviewerGender}, #{interviewerAppearance}, #{createdAt}, #{resumeUri}, #{position})")
    void insert(InterviewSetting setting);

}
