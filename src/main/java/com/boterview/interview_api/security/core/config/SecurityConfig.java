package com.boterview.interview_api.security.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;
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

import com.boterview.interview_api.security.authentication.oauth.handler.OAuth2FailureHandler;
import com.boterview.interview_api.security.authentication.oauth.handler.OAuth2SuccessHandler;
import com.boterview.interview_api.security.authentication.oauth.repository.HttpCookieOAuth2AuthorizationRequestRepository;
import com.boterview.interview_api.security.authentication.oauth.service.BotOAuth2UserService;
import com.boterview.interview_api.security.authentication.oauth.service.BotOidcUserService;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Profile({ "dev", "prod" })
public class SecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        Environment environment,

                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        BotOAuth2UserService oAuth2UserService,
                        BotOidcUserService oidcUserService,
                        OAuth2SuccessHandler oAuth2SuccessHandler,
                        OAuth2FailureHandler oAuth2FailureHandler,
                        HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository,
                        JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                        JwtAccessDeniedHandler jwtAccessDeniedHandler) throws Exception {

                boolean isTestProfile = Arrays.asList(environment.getActiveProfiles()).contains("test");

                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers(SecurityPaths.PUBLIC_PATHS).permitAll()
                                                .requestMatchers(HttpMethod.POST,
                                                                SecurityPaths.MethodSpecific.POST_ONLY)
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(e -> e
                                                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                                                .accessDeniedHandler(jwtAccessDeniedHandler))
                                .oauth2Login(login -> login
                                                .authorizationEndpoint(endpoint -> endpoint
                                                                .baseUri("/api/auth/oauth")
                                                                .authorizationRequestRepository(
                                                                                cookieAuthorizationRequestRepository))
                                                .userInfoEndpoint(info -> info
                                                                .userService(oAuth2UserService)
                                                                .oidcUserService(oidcUserService))
                                                .successHandler(oAuth2SuccessHandler)
                                                .failureHandler(oAuth2FailureHandler))
                                .addFilterAfter(jwtAuthenticationFilter, ExceptionTranslationFilter.class);

                // formLogin과 logout은 test profile이 아닐 때만 활성화

                return http.build();
        }
}
