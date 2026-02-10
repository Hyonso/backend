package com.boterview.interview_api.domain.dashboard.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;
import com.boterview.interview_api.domain.dashboard.dto.DashboardResponseDto;
import com.boterview.interview_api.domain.dashboard.dto.DashboardSettingResponseDto;
import com.boterview.interview_api.domain.dashboard.repository.DashboardMapper;
import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.repository.UserMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardMapper dashboardMapper;
    private final UserMapper userMapper;

    public DashboardResponseDto getDashboard(String userId) {
        User user = userMapper.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        DashboardResponseDto.StatsDto stats = dashboardMapper.findStatsByUserId(userId);

        List<DashboardResponseDto.RecentInterviewDto> recentInterviews =
                dashboardMapper.findRecentInterviewsByUserId(userId);

        for (DashboardResponseDto.RecentInterviewDto interview : recentInterviews) {
            List<String> skills = dashboardMapper.findSkillsBySettingId(interview.getSettingId());
            interview.setSkills(skills);

            Integer overallScore = dashboardMapper.findOverallScoreByInterviewId(interview.getInterviewId());
            interview.setScore(overallScore != null ? overallScore : 0);

            if (interview.getCreatedAt() != null) {
                long daysSinceCreated = ChronoUnit.DAYS.between(
                        interview.getCreatedAt().toLocalDate(), LocalDate.now());
                interview.setRemainDate(Math.max(0, (int) (30 - daysSinceCreated)));
            } else {
                interview.setRemainDate(0);
            }
        }

        return DashboardResponseDto.builder()
                .user(DashboardResponseDto.UserDto.builder()
                        .userId(user.getUserId())
                        .name(user.getName())
                        .build())
                .status(stats)
                .recentInterviews(recentInterviews)
                .build();
    }

    public DashboardSettingResponseDto getSettingDetail(String settingId, String userId) {
        DashboardSettingResponseDto setting = dashboardMapper.findSettingById(settingId)
                .orElseThrow(() -> new BaseException(ErrorCode.RESOURCE_NOT_FOUND));

        if (!setting.getUserId().equals(userId)) {
            throw new BaseException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        setting.setSkills(dashboardMapper.findSkillDtosBySettingId(settingId));
        setting.setPreQuestions(dashboardMapper.findPreQuestionsBySettingId(settingId));

        return setting;
    }
}
