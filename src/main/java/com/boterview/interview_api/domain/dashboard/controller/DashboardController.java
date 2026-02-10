package com.boterview.interview_api.domain.dashboard.controller;

import com.boterview.interview_api.domain.dashboard.dto.DashboardInterviewDetailResponseDto;
import com.boterview.interview_api.domain.dashboard.dto.DashboardResponseDto;
import com.boterview.interview_api.domain.dashboard.dto.DashboardSettingResponseDto;
import com.boterview.interview_api.domain.dashboard.service.DashboardService;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<DashboardResponseDto> getDashboard(
            @AuthenticationPrincipal BotUserDetails userDetails) {
        String userId = userDetails.getUserDto().getUserId();
        return ResponseEntity.ok(dashboardService.getDashboard(userId));
    }

    @GetMapping("/{interviewId}")
    public ResponseEntity<DashboardInterviewDetailResponseDto> getInterviewDetail(
            @PathVariable String interviewId,
            @AuthenticationPrincipal BotUserDetails userDetails) {
        String userId = userDetails.getUserDto().getUserId();
        return ResponseEntity.ok(dashboardService.getInterviewDetail(interviewId, userId));
    }

    @GetMapping("/setting/{settingId}")
    public ResponseEntity<DashboardSettingResponseDto> getSettingDetail(
            @PathVariable String settingId,
            @AuthenticationPrincipal BotUserDetails userDetails) {
        String userId = userDetails.getUserDto().getUserId();
        return ResponseEntity.ok(dashboardService.getSettingDetail(settingId, userId));
    }
}
