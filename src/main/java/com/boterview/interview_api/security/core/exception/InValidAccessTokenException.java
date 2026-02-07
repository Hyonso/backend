package com.boterview.interview_api.security.core.exception;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;

public class InValidAccessTokenException extends BaseException {
    public InValidAccessTokenException() {
        super(ErrorCode.INVALID_ACCESS_TOKEN);
    }
}
