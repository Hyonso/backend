package com.boterview.interview_api.domain.interview.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;
import com.boterview.interview_api.domain.interview.dto.InterviewResultResponseDto;
import com.boterview.interview_api.domain.interview.entity.Interview;
import com.boterview.interview_api.domain.interview.entity.InterviewQuestion;
import com.boterview.interview_api.domain.interview.entity.InterviewScore;
import com.boterview.interview_api.domain.interview.repository.InterviewMapper;
import com.boterview.interview_api.domain.interview.repository.InterviewQuestionMapper;
import com.boterview.interview_api.domain.interview.repository.InterviewResultMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InterviewResultService {

    private final InterviewMapper interviewMapper;
    private final InterviewQuestionMapper interviewQuestionMapper;
    private final InterviewResultMapper interviewResultMapper;

    public InterviewResultResponseDto getInterviewResult(String interviewId) {
        Interview interview = interviewMapper.findById(interviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.RESOURCE_NOT_FOUND));

        String userId = interviewMapper.findUserIdByInterviewId(interviewId)
                .orElseThrow(() -> new BaseException(ErrorCode.RESOURCE_NOT_FOUND));

        List<InterviewQuestion> questions = interviewQuestionMapper.findByInterviewId(interviewId);
        List<InterviewScore> scores = interviewResultMapper.findByInterviewId(interviewId);

        List<InterviewResultResponseDto.QuestionDto> questionDtos = questions.stream()
                .map(q -> InterviewResultResponseDto.QuestionDto.builder()
                        .questionId(q.getQuestionId())
                        .question(q.getQuestion())
                        .answer(q.getAnswer())
                        .intervalTime(q.getElapsedTime())
                        .build())
                .toList();

        List<InterviewResultResponseDto.ScoreDto> scoreDtos = scores.stream()
                .map(s -> InterviewResultResponseDto.ScoreDto.builder()
                        .scoreId(s.getScoreId())
                        .scoreType(s.getScoreType().name())
                        .score(s.getScore())
                        .feedback(s.getEvaludation())
                        .build())
                .toList();

        return InterviewResultResponseDto.builder()
                .interviewId(interview.getInterviewId())
                .userId(userId)
                .progressTime(interview.getDuration())
                .feedback(interview.getAiOverallReview())
                .createdAt(interview.getCreatedAt())
                .interviewQuestion(questionDtos)
                .scores(scoreDtos)
                .build();
    }
}
