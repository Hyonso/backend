package com.boterview.interview_api.security.authentication.jwt.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws java.io.IOException {
		response.setContentType("application/json;charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		String authorizationHeader = request.getHeader("Authorization");

		// 토큰이 없거나 형식이 올바르지 않은 경우 (A001 아님, 그냥 인증 필요)
		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			String json = "{\"code\": \"A000\", \"message\": \"인증이 필요합니다\"}";
			response.getWriter().write(json);
			return;
		}

		// 토큰이 있지만 유효하지 않은 경우 (A001)
		com.boterview.interview_api.common.exception.ErrorCode errorCode = com.boterview.interview_api.common.exception.ErrorCode.INVALID_ACCESS_TOKEN;
		String json = String.format("{\"code\": \"%s\", \"message\": \"%s\"}", errorCode.getCode(),
				errorCode.getMessage());
		response.getWriter().write(json);
	}
}
