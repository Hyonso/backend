package com.boterview.interview_api.security.api.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {
    private String accessToken;
    private UserInfo user;

    @Getter
    @AllArgsConstructor
    public static class UserInfo {
        private String userId;
        private String name;
        private String email;
    }
}
