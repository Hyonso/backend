package com.boterview.interview_api.domain.interviewSetting.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.web.servlet.MockMvc;

import com.boterview.interview_api.domain.interviewSetting.service.InterviewSettingService;
import com.boterview.interview_api.domain.user.entity.OAuthProvider;
import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;
import com.boterview.interview_api.security.authentication.jwt.registry.JwtRegistry;
import com.boterview.interview_api.security.core.dto.SecurityUserDto;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

@WebMvcTest(InterviewSettingController.class)
@AutoConfigureMockMvc(addFilters = false)
class InterviewSettingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InterviewSettingService interviewSettingService;

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

    private String createRequestJson() {
        return """
                {
                    "target_position": "BACKEND_DEVELOPER",
                    "skills": ["Java", "Spring"],
                    "personality_type": "FORMAL",
                    "appearance_style": "REAL",
                    "gender": "MALE",
                    "question_count": 3,
                    "pre_questions": [
                        {
                            "question": "자기소개 해주세요",
                            "answer": "안녕하세요"
                        }
                    ]
                }
                """;
    }

    @Test
    @DisplayName("resume 포함 정상 요청 -> 200 OK")
    void saveSettings_withResume() throws Exception {
        setAuth();

        MockMultipartFile data = new MockMultipartFile(
                "data", "", "application/json", createRequestJson().getBytes());
        MockMultipartFile resume = new MockMultipartFile(
                "resume", "resume.pdf", "application/pdf", "fake-pdf".getBytes());

        mockMvc.perform(multipart("/api/interview-settings")
                        .file(data)
                        .file(resume))
                .andDo(print())
                .andExpect(status().isOk());

        verify(interviewSettingService).saveSettings(eq("test-user-id"), any(), any());
    }

    @Test
    @DisplayName("resume 없이 정상 요청 -> 200 OK")
    void saveSettings_withoutResume() throws Exception {
        setAuth();

        MockMultipartFile data = new MockMultipartFile(
                "data", "", "application/json", createRequestJson().getBytes());

        mockMvc.perform(multipart("/api/interview-settings")
                        .file(data))
                .andDo(print())
                .andExpect(status().isOk());

        verify(interviewSettingService).saveSettings(eq("test-user-id"), any(), isNull());
    }

    @Test
    @DisplayName("인증 없이 요청 -> 401 Unauthorized")
    void saveSettings_noAuth() throws Exception {
        SecurityContextHolder.clearContext();

        MockMultipartFile data = new MockMultipartFile(
                "data", "", "application/json", createRequestJson().getBytes());

        mockMvc.perform(multipart("/api/interview-settings")
                        .file(data))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
