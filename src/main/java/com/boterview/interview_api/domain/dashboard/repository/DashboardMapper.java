package com.boterview.interview_api.domain.dashboard.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.boterview.interview_api.domain.dashboard.dto.DashboardResponseDto;
import com.boterview.interview_api.domain.dashboard.dto.DashboardSettingResponseDto;

@Mapper
public interface DashboardMapper {

    @Select("SELECT COUNT(*) AS total_interviews, " +
            "COALESCE(AVG(os.score), 0) AS average_score, " +
            "COALESCE(SUM(i.duration), 0) / 60000 AS total_time_minutes, " +
            "COALESCE(MAX(os.score), 0) AS best_score " +
            "FROM interview i " +
            "JOIN interview_setting s ON i.setting_id = s.setting_id " +
            "LEFT JOIN interview_score os ON i.interview_id = os.interview_id AND os.score_type = 'OVERALL' " +
            "WHERE s.user_id = #{userId}")
    @Results({
            @Result(column = "total_interviews", property = "totalInterviews"),
            @Result(column = "average_score", property = "averageScore"),
            @Result(column = "total_time_minutes", property = "totalTimeMinutes"),
            @Result(column = "best_score", property = "bestScore")
    })
    DashboardResponseDto.StatsDto findStatsByUserId(@Param("userId") String userId);

    @Select("SELECT i.interview_id, i.setting_id, i.created_at, i.interview_name AS title, " +
            "s.position AS job, s.question_count, i.duration / 60000 AS duration_minutes " +
            "FROM interview i " +
            "JOIN interview_setting s ON i.setting_id = s.setting_id " +
            "WHERE s.user_id = #{userId} " +
            "ORDER BY i.created_at DESC")
    @Results({
            @Result(column = "interview_id", property = "interviewId"),
            @Result(column = "setting_id", property = "settingId"),
            @Result(column = "created_at", property = "createdAt"),
            @Result(column = "title", property = "title"),
            @Result(column = "job", property = "job"),
            @Result(column = "question_count", property = "questionCount"),
            @Result(column = "duration_minutes", property = "durationMinutes")
    })
    List<DashboardResponseDto.RecentInterviewDto> findRecentInterviewsByUserId(@Param("userId") String userId);

    @Select("SELECT sk.skill " +
            "FROM setting_skill ss " +
            "JOIN skill sk ON ss.skill_id = sk.skill_id " +
            "WHERE ss.setting_id = #{settingId}")
    List<String> findSkillsBySettingId(@Param("settingId") String settingId);

    @Select("SELECT score FROM interview_score " +
            "WHERE interview_id = #{interviewId} AND score_type = 'OVERALL'")
    Integer findOverallScoreByInterviewId(@Param("interviewId") String interviewId);

    @Select("SELECT setting_id, user_id, question_count, interviewer_style, " +
            "interviewer_gender, interviewer_appearance, position AS job, " +
            "resume_uri AS resume_s3_path, created_at " +
            "FROM interview_setting WHERE setting_id = #{settingId}")
    @Results({
            @Result(column = "setting_id", property = "settingId"),
            @Result(column = "user_id", property = "userId"),
            @Result(column = "question_count", property = "questionCount"),
            @Result(column = "interviewer_style", property = "interviewerStyle"),
            @Result(column = "interviewer_gender", property = "interviewerGender"),
            @Result(column = "interviewer_appearance", property = "interviewerAppearance"),
            @Result(column = "job", property = "job"),
            @Result(column = "resume_s3_path", property = "resumeS3Path"),
            @Result(column = "created_at", property = "createdAt")
    })
    Optional<DashboardSettingResponseDto> findSettingById(@Param("settingId") String settingId);

    @Select("SELECT sk.skill_id, sk.skill AS name " +
            "FROM setting_skill ss " +
            "JOIN skill sk ON ss.skill_id = sk.skill_id " +
            "WHERE ss.setting_id = #{settingId}")
    @Results({
            @Result(column = "skill_id", property = "skillId"),
            @Result(column = "name", property = "name")
    })
    List<DashboardSettingResponseDto.SkillDto> findSkillDtosBySettingId(@Param("settingId") String settingId);

    @Select("SELECT pre_question_id, question, answer " +
            "FROM pre_question WHERE setting_id = #{settingId}")
    @Results({
            @Result(column = "pre_question_id", property = "preQuestionId"),
            @Result(column = "question", property = "question"),
            @Result(column = "answer", property = "answer")
    })
    List<DashboardSettingResponseDto.PreQuestionDto> findPreQuestionsBySettingId(@Param("settingId") String settingId);
}
