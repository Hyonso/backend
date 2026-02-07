package com.boterview.interview_api.security.authentication.local.exception;

import org.springframework.security.core.AuthenticationException;

public class InValidCredentialException extends AuthenticationException {
    public InValidCredentialException(String message) {
        super(message);
    }
}
