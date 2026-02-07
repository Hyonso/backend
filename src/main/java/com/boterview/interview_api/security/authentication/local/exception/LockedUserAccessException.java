package com.boterview.interview_api.security.authentication.local.exception;

import org.springframework.security.core.AuthenticationException;

public class LockedUserAccessException extends AuthenticationException {
    public LockedUserAccessException(String message) {
        super(message);
    }
}
