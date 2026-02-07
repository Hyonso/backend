package com.boterview.interview_api.security.core.exception;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;

public class TokenGenerateException extends BaseException {

    public TokenGenerateException() {
        super(ErrorCode.TOKEN_GENERATE_FAIL);
    }

    public TokenGenerateException(Throwable cause) {
        super(ErrorCode.TOKEN_GENERATE_FAIL);
        initCause(cause);
    }
}
