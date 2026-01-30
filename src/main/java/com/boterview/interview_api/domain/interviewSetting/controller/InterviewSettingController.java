package com.boterview.interview_api.domain.interviewSetting.controller;

import com.boterview.interview_api.domain.interviewSetting.service.InterviewSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interview-settings")
@RequiredArgsConstructor
public class InterviewSettingController {

    private final InterviewSettingService interviewSettingService;
}
