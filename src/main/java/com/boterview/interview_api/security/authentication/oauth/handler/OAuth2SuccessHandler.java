package com.boterview.interview_api.security.authentication.oauth.handler;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.boterview.interview_api.security.authentication.jwt.dto.JwtInformation;
import com.boterview.interview_api.security.core.exception.UnexpectedPrincipalException;
import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;
import com.boterview.interview_api.security.authentication.jwt.registry.JwtRegistry;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException {

        if (!(authentication.getPrincipal() instanceof BotUserDetails userDetails)) {
            throw new UnexpectedPrincipalException();
        }

        String accessToken = tokenProvider.generateAccessToken(userDetails);
        String refreshToken = tokenProvider.generateRefreshToken(userDetails);

        JwtInformation info = new JwtInformation(userDetails.getUserDto(), accessToken, refreshToken);
        jwtRegistry.registerJwtInformation(info);

        Cookie refreshCookie = tokenProvider.generateRefreshTokenCookie(refreshToken);
        response.addCookie(refreshCookie);
        response.sendRedirect(resolveRedirectUrl(request, "/dashboard"));
    }

    private String resolveRedirectUrl(HttpServletRequest request, String path) {
        String proto = request.getHeader("X-Forwarded-Proto");
        String host = request.getHeader("X-Forwarded-Host");
        if (proto == null)
            proto = request.getScheme();
        if (host == null)
            host = request.getHeader("Host");
        if (host == null)
            host = request.getServerName();
        return proto + "://" + host + path;
    }
}
