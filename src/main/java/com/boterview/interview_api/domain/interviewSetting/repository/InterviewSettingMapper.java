package com.boterview.interview_api.domain.interviewSetting.repository;

import com.boterview.interview_api.domain.interviewSetting.entity.InterviewSetting;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface InterviewSettingMapper {


    @Insert("INSERT INTO interview_setting (setting_id, user_id, question_count, interviewer_tone, " +
            "interviewer_gender, interviewer_appearance, created_at, resume_s3_path, job_type) " +
            "VALUES (#{settingId}, #{userId}, #{questionCount}, #{interviewerTone}, " +
            "#{interviewerGender}, #{interviewerAppearance}, #{createdAt}, #{resumeS3Path}, #{jobType})")
    void insert(InterviewSetting setting);

}
