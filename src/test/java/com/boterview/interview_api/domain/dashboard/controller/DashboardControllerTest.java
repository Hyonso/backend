package com.boterview.interview_api.domain.dashboard.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.boterview.interview_api.domain.dashboard.dto.DashboardResponseDto;
import com.boterview.interview_api.domain.dashboard.service.DashboardService;
import com.boterview.interview_api.domain.user.entity.OAuthProvider;
import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;
import com.boterview.interview_api.security.authentication.jwt.registry.JwtRegistry;
import com.boterview.interview_api.security.core.dto.SecurityUserDto;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardService dashboardService;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private JwtRegistry jwtRegistry;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private void setAuth() {
        SecurityUserDto userDto = new SecurityUserDto("test-user-id", "test@test.com", "tester", OAuthProvider.GOOGLE);
        BotUserDetails userDetails = new BotUserDetails(userDto, "password");
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("대시보드 조회 -> 200 OK")
    void getDashboard_success() throws Exception {
        setAuth();

        DashboardResponseDto response = DashboardResponseDto.builder()
                .user(DashboardResponseDto.UserDto.builder()
                        .userId("test-user-id")
                        .name("tester")
                        .build())
                .status(DashboardResponseDto.StatsDto.builder()
                        .totalInterviews(3)
                        .averageScore(82)
                        .totalTimeMinutes(45)
                        .bestScore(87)
                        .build())
                .recentInterviews(List.of(
                        DashboardResponseDto.RecentInterviewDto.builder()
                                .interviewId("interview-1")
                                .settingId("setting-1")
                                .createdAt(LocalDateTime.of(2026, 1, 30, 12, 30, 0))
                                .title("Backend Engineer | Spring")
                                .job("BACKEND")
                                .skills(List.of("Spring", "Java"))
                                .durationMinutes(12)
                                .questionCount(5)
                                .score(87)
                                .remainDate(12)
                                .build()
                ))
                .build();

        when(dashboardService.getDashboard("test-user-id")).thenReturn(response);

        mockMvc.perform(get("/api/dashboard"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.user_id").value("test-user-id"))
                .andExpect(jsonPath("$.user.name").value("tester"))
                .andExpect(jsonPath("$.stats.total_interviews").value(3))
                .andExpect(jsonPath("$.stats.average_score").value(82))
                .andExpect(jsonPath("$.stats.total_time_minutes").value(45))
                .andExpect(jsonPath("$.stats.best_score").value(87))
                .andExpect(jsonPath("$.recent_interviews[0].interview_id").value("interview-1"))
                .andExpect(jsonPath("$.recent_interviews[0].setting_id").value("setting-1"))
                .andExpect(jsonPath("$.recent_interviews[0].title").value("Backend Engineer | Spring"))
                .andExpect(jsonPath("$.recent_interviews[0].job").value("BACKEND"))
                .andExpect(jsonPath("$.recent_interviews[0].skills[0]").value("Spring"))
                .andExpect(jsonPath("$.recent_interviews[0].skills[1]").value("Java"))
                .andExpect(jsonPath("$.recent_interviews[0].duration_minutes").value(12))
                .andExpect(jsonPath("$.recent_interviews[0].question_count").value(5))
                .andExpect(jsonPath("$.recent_interviews[0].score").value(87))
                .andExpect(jsonPath("$.recent_interviews[0].remain_date").value(12));
    }

    @Test
    @DisplayName("인증 없이 대시보드 조회 -> 500")
    void getDashboard_noAuth() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(get("/api/dashboard"))
                .andDo(print())
                .andExpect(status().is5xxServerError());
    }
}
