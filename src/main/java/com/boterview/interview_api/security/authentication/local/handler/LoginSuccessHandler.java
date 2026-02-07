package com.boterview.interview_api.security.authentication.local.handler;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.boterview.interview_api.security.authentication.jwt.dto.JwtDto;
import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;
import com.boterview.interview_api.security.core.exception.UnexpectedPrincipalException;
import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;
import com.boterview.interview_api.security.authentication.jwt.registry.JwtRegistry;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;
    private final ObjectMapper objectMapper;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {

        if (!(authentication.getPrincipal() instanceof BotUserDetails userDetails)) {
            throw new UnexpectedPrincipalException();
        }

        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);
        JwtDto jwtDto = new JwtDto(userDetails.getUserDto(), accessToken);
        JwtInformation info = new JwtInformation(userDetails.getUserDto(), accessToken, refreshToken);
        jwtRegistry.registerJwtInformation(info);

        Cookie refreshCookie = tokenProvider.generateRefreshTokenCookie(refreshToken);
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addCookie(refreshCookie);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(objectMapper.writeValueAsString(jwtDto));
    }
}
