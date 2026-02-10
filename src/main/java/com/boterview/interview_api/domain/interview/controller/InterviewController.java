package com.boterview.interview_api.domain.interview.controller;

import com.boterview.interview_api.domain.interview.dto.InterviewResultResponseDto;
import com.boterview.interview_api.domain.interview.service.InterviewResultService;
import com.boterview.interview_api.domain.interview.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewResultService interviewResultService;

    @GetMapping("/results/{interviewId}")
    public ResponseEntity<InterviewResultResponseDto> getInterviewResult(
            @PathVariable String interviewId) {
        InterviewResultResponseDto result = interviewResultService.getInterviewResult(interviewId);
        return ResponseEntity.ok(result);
    }
}
