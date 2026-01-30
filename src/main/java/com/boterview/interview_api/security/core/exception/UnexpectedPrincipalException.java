package com.boterview.interview_api.security.core.exception;

import com.boterview.interview_api.common.exception.BaseException;
import com.boterview.interview_api.common.exception.ErrorCode;

public class UnexpectedPrincipalException extends BaseException {
    public UnexpectedPrincipalException() {
        super(ErrorCode.UNEXPECTED_PRINCIPAL);
    }
}
