package com.boterview.interview_api.security.authentication.local.handler;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LogoutSuccessHandler implements org.springframework.security.web.authentication.logout.LogoutSuccessHandler {

    private final JwtTokenProvider tokenProvider;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) {
        Cookie cookie = tokenProvider.generateRefreshTokenExpirationCookie();
        response.addCookie(cookie);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
