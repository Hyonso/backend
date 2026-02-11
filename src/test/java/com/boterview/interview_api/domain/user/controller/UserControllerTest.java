package com.boterview.interview_api.domain.user.controller;

import com.boterview.interview_api.domain.user.dto.UserResponseDto;
import com.boterview.interview_api.domain.user.dto.UserUpdateDto;
import com.boterview.interview_api.domain.user.entity.User;
import com.boterview.interview_api.domain.user.service.UserService;
import com.boterview.interview_api.security.core.dto.SecurityUserDto;
import com.boterview.interview_api.security.core.principal.BotUserDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.boterview.interview_api.security.authentication.jwt.registry.TestJwtRegistryConfig;
import com.boterview.interview_api.config.TestSecurityConfig;
import org.springframework.context.annotation.Import;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import({ TestJwtRegistryConfig.class, TestSecurityConfig.class })
class UserControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private UserService userService;

        @Test
        @DisplayName("GET /api/users - 내 정보 조회 성공")
        void getUser_Success() throws Exception {
                // Given
                String userId = "test-user-id";
                String email = "test@example.com";
                String name = "Test User";

                SecurityUserDto userDto = SecurityUserDto.builder()
                                .userId(userId)
                                .email(email)
                                .name(name)
                                .build();
                BotUserDetails userDetails = new BotUserDetails(userDto, "password");

                UserResponseDto responseDto = UserResponseDto.builder()
                                .userId(userId)
                                .email(email)
                                .name(name)
                                .build();

                given(userService.getUser(userId)).willReturn(responseDto);

                // When & Then
                mockMvc.perform(get("/api/users")
                                .with(user(userDetails)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.userId").value(userId))
                                .andExpect(jsonPath("$.email").value(email))
                                .andExpect(jsonPath("$.name").value(name));
        }

        @Test
        @DisplayName("PUT /api/users - 회원 정보 수정 성공")
        void updateUser_Success() throws Exception {
                // Given
                String userId = "test-user-id";
                String newName = "Updated Name";

                SecurityUserDto userDto = SecurityUserDto.builder()
                                .userId(userId)
                                .email("test@example.com")
                                .name("Old Name")
                                .build();
                BotUserDetails userDetails = new BotUserDetails(userDto, "password");

                UserUpdateDto updateDto = new UserUpdateDto(newName, null);

                UserResponseDto responseDto = UserResponseDto.builder()
                                .userId(userId)
                                .email("test@example.com")
                                .name(newName)
                                .build();

                given(userService.updateUser(eq(userId), any(UserUpdateDto.class))).willReturn(responseDto);

                // When & Then
                mockMvc.perform(put("/api/users")
                                .with(user(userDetails))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(updateDto)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.updated").value(true));
        }

        @Test
        @DisplayName("DELETE /api/users - 회원 탈퇴 성공")
        void deleteUser_Success() throws Exception {
                // Given
                String userId = "test-user-id";

                SecurityUserDto userDto = SecurityUserDto.builder()
                                .userId(userId)
                                .email("test@example.com")
                                .name("Test User")
                                .build();
                BotUserDetails userDetails = new BotUserDetails(userDto, "password");

                // When & Then
                mockMvc.perform(delete("/api/users")
                                .with(user(userDetails)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.deleted").value(true));
        }
}
