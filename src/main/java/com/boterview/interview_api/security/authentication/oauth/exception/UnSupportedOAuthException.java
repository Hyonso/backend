package com.boterview.interview_api.security.authentication.oauth.exception;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;

public class UnSupportedOAuthException extends BaseException {
    public UnSupportedOAuthException(String provider) {
        super(ErrorCode.UNSUPPORTED_OAUTH);
    }
}
