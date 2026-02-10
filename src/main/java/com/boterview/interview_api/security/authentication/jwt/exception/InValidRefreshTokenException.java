package com.boterview.interview_api.security.authentication.jwt.exception;

import org.springframework.security.core.AuthenticationException;

public class InValidRefreshTokenException extends AuthenticationException {
    public InValidRefreshTokenException(String message) {
        super(message);
    }
}
