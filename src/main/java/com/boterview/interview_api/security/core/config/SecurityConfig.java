package com.boterview.interview_api.security.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import com.boterview.interview_api.security.authentication.jwt.filter.JwtAuthenticationFilter;
import com.boterview.interview_api.security.authentication.jwt.handler.JwtAccessDeniedHandler;
import com.boterview.interview_api.security.authentication.jwt.handler.JwtAuthenticationEntryPoint;
import com.boterview.interview_api.security.authentication.local.handler.LoginSuccessHandler;
import com.boterview.interview_api.security.authentication.local.handler.LogoutSuccessHandler;
import com.boterview.interview_api.security.authentication.oauth.handler.OAuth2FailureHandler;
import com.boterview.interview_api.security.authentication.oauth.handler.OAuth2SuccessHandler;
import com.boterview.interview_api.security.authentication.oauth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.boterview.interview_api.security.authentication.oauth.service.BotOAuth2UserService;
import com.boterview.interview_api.security.authentication.oauth.service.BotOidcUserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            LoginSuccessHandler loginSuccessHandler,
            LogoutSuccessHandler logoutSuccessHandler,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            BotOAuth2UserService oAuth2UserService,
            BotOidcUserService oidcUserService,
            OAuth2SuccessHandler oAuth2SuccessHandler,
            OAuth2FailureHandler oAuth2FailureHandler,
            HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            JwtAccessDeniedHandler jwtAccessDeniedHandler
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityPaths.PUBLIC_PATHS).permitAll()
                        .requestMatchers(HttpMethod.POST, SecurityPaths.MethodSpecific.POST_ONLY).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginProcessingUrl("/api/auth/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .successHandler(loginSuccessHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(logoutSuccessHandler)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                )
                .oauth2Login(login -> login
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(cookieAuthorizationRequestRepository)
                        )
                        .userInfoEndpoint(info -> info
                                .userService(oAuth2UserService)
                                .oidcUserService(oidcUserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )
                .addFilterAfter(jwtAuthenticationFilter, ExceptionTranslationFilter.class);

        return http.build();
    }
}
