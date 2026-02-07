package com.boterview.interview_api.security.authentication.jwt.dto;

import com.boterview.interview_api.security.core.dto.SecurityUserDto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtDto {
    private SecurityUserDto userDto;
    private String accessToken;
}
