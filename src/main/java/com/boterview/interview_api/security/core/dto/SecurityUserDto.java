package com.boterview.interview_api.security.core.dto;

import com.boterview.interview_api.domain.user.entity.OAuthProvider;
import com.boterview.interview_api.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SecurityUserDto {
    private String userId;
    private String email;
    private String name;
    private OAuthProvider oauth;

    public static SecurityUserDto from(User user) {
        return new SecurityUserDto(
                user.getUserId(),
                user.getEmail(),
                user.getName(),
                user.getOauth()
        );
    }
}
