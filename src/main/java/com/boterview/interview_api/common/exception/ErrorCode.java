package com.boterview.interview_api.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력입니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 오류가 발생했습니다"),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "C003", "요청한 리소스를 찾을 수 없습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C004", "허용되지 않은 HTTP 메서드입니다"),

    // Auth
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "A001", "유효하지 않은 액세스 토큰입니다"),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "A002", "유효하지 않은 리프레시 토큰입니다"),
    TOKEN_GENERATE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "A003", "토큰 생성에 실패했습니다"),
    UNEXPECTED_PRINCIPAL(HttpStatus.INTERNAL_SERVER_ERROR, "A004", "예상치 못한 인증 주체입니다"),
    INVALID_CREDENTIAL(HttpStatus.UNAUTHORIZED, "A005", "이메일 또는 비밀번호가 올바르지 않습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "A006", "사용자를 찾을 수 없습니다"),
    UNSUPPORTED_OAUTH(HttpStatus.BAD_REQUEST, "A007", "지원하지 않는 OAuth 제공자입니다"),
    AUTH_PASSWORD_ERROR(HttpStatus.BAD_REQUEST, "A008", "소셜 로그인 사용자는 비밀번호를 변경할 수 없습니다"),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "A009", "이미 존재하는 이메일입니다"),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
