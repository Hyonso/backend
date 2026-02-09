package com.boterview.interview_api.security.authentication.local.handler;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.boterview.interview_api.common.exception.ErrorCode;
import com.boterview.interview_api.common.exception.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginFailureHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException {
        log.error("Login failed: {}", exception.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        // 기본적으로 자격 증명 실패 에러 코드를 반환
        ErrorCode errorCode = ErrorCode.INVALID_CREDENTIAL;
        ErrorResponse errorResponse = ErrorResponse.of(errorCode.getCode(), errorCode.getMessage());

        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
