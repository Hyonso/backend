package com.boterview.interview_api.domain.interviewSetting.controller;

import com.boterview.interview_api.domain.interviewSetting.service.InterviewSettingService;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.boterview.interview_api.domain.interviewSetting.dto.InterviewSettingRequestDto;

@RestController
@RequestMapping("/api/interview-settings")
@RequiredArgsConstructor
public class InterviewSettingController {

    private final InterviewSettingService interviewSettingService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> setInterview(
        @AuthenticationPrincipal BotUserDetails userDetails,
        @RequestPart("data") InterviewSettingRequestDto dto,
        @RequestPart(value = "resume", required = false) MultipartFile resume
    ){
        String userId = userDetails.getUserDto().getUserId();
        interviewSettingService.saveSettings(userId, dto, resume);
        return ResponseEntity.ok().build();
    }
}
