package com.boterview.interview_api.security.core.exception;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;

public class InValidRefreshTokenException extends BaseException {
    public InValidRefreshTokenException() {
        super(ErrorCode.INVALID_REFRESH_TOKEN);
    }
}
