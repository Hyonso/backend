package com.boterview.interview_api.security.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Profile({ "local" })
public class DevSecurityConfig {

        @Bean
        public SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        com.boterview.interview_api.security.authentication.oauth.service.BotOAuth2UserService oAuth2UserService,
                        com.boterview.interview_api.security.authentication.oauth.service.BotOidcUserService oidcUserService,
                        com.boterview.interview_api.security.authentication.oauth.handler.OAuth2SuccessHandler oAuth2SuccessHandler,
                        com.boterview.interview_api.security.authentication.oauth.handler.OAuth2FailureHandler oAuth2FailureHandler,
                        com.boterview.interview_api.security.authentication.oauth.repository.HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository)
                        throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .anyRequest().permitAll())
                                .csrf(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
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
                                .formLogin(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable);

                return http.build();
        }
}
