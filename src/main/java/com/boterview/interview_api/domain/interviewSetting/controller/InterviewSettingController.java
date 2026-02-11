package com.boterview.interview_api.domain.interviewSetting.controller;

import com.boterview.interview_api.domain.interviewSetting.dto.InterviewSettingResponseDto;
import com.boterview.interview_api.domain.interviewSetting.service.InterviewSettingService;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

import java.util.Map;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.boterview.interview_api.domain.interviewSetting.dto.InterviewSettingRequestDto;

@RestController
@RequestMapping({ "/api/interview-settings", "/api/interviews-settings" })
@RequiredArgsConstructor
public class InterviewSettingController {

    private final InterviewSettingService interviewSettingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InterviewSettingResponseDto> setInterviewMultipart(
            @AuthenticationPrincipal BotUserDetails userDetails,
            @RequestPart("data") InterviewSettingRequestDto dto,
            @RequestPart(value = "resume", required = false) MultipartFile resume) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = userDetails.getUserDto().getUserId();
        String settingId = interviewSettingService.saveSettings(userId, dto, resume);
        InterviewSettingResponseDto res = InterviewSettingResponseDto.builder().settingId(settingId).build();
        return ResponseEntity.ok(res);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InterviewSettingResponseDto> setInterviewJson(
            @AuthenticationPrincipal BotUserDetails userDetails,
            @RequestBody InterviewSettingRequestDto dto) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String userId = userDetails.getUserDto().getUserId();
        String settingId = interviewSettingService.saveSettings(userId, dto, null);
        InterviewSettingResponseDto res = InterviewSettingResponseDto.builder().settingId(settingId).build();
        return ResponseEntity.ok(res);
    }
}
