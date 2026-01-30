package com.boterview.interview_api.security.authentication.jwt.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.boterview.interview_api.security.authentication.jwt.exception.InValidAccessTokenException;
import com.boterview.interview_api.security.authentication.jwt.provider.JwtTokenProvider;
import com.boterview.interview_api.security.authentication.jwt.registry.JwtRegistry;
import com.boterview.interview_api.security.core.config.SecurityPaths;
import com.boterview.interview_api.security.core.principal.BotUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final int BEARER_PREFIX_LENGTH = 7;

    private final JwtTokenProvider tokenProvider;
    private final JwtRegistry jwtRegistry;
    private final UserDetailsService userDetailsService;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        String method = request.getMethod();
        return SecurityPaths.isPublicPath(path, method);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = header.substring(BEARER_PREFIX_LENGTH);

        if (!tokenProvider.validateAccessToken(accessToken)) {
            throw new InValidAccessTokenException("Invalid access token: expired");
        }

        if (!jwtRegistry.hasActiveJwtInformationByAccessToken(accessToken)) {
            throw new InValidAccessTokenException("Inactive access token: not registered");
        }

        String email = tokenProvider.getSubject(accessToken);
        BotUserDetails userDetails = (BotUserDetails) userDetailsService.loadUserByUsername(email);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }
}
