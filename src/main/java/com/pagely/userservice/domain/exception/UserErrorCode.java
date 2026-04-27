package com.pagely.userservice.domain.exception;

import com.pagely.common.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * User 도메인 에러 코드.
 *
 * <p>공통 모듈의 {@link ErrorCode} 를 구현하여
 * GlobalExceptionHandler 가 통일된 응답 포맷으로 변환할 수 있게 한다.</p>
 */
@Getter
public enum UserErrorCode implements ErrorCode {

    // 409 - 회원가입
    DUPLICATE_LOGIN_ID("이미 사용 중인 로그인 아이디입니다.", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    DUPLICATE_NICKNAME("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),

    // 404 - 조회
    USER_NOT_FOUND("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 401 - 인증
    INVALID_PASSWORD("비밀번호가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    UserErrorCode(String message, HttpStatus httpStatus) {
        this.code = this.name();
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
