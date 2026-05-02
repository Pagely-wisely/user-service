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

    // 400 - 잘못된 인수
    PASSWORD_REQUIRED("비밀번호는 필수입니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_LENGTH_INVALID("비밀번호는 10자 이상 64자 이하여야 합니다.", HttpStatus.BAD_REQUEST),
    PASSWORD_WHITESPACE_NOT_ALLOWED("비밀번호에 공백은 사용할 수 없습니다.", HttpStatus.BAD_REQUEST),
    NICKNAME_CHANGE_LIMIT("닉네임은 변경으로부터 30일 이내에 수정할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // 409 - 중복 데이터
    DUPLICATE("데이터 중복 오류 (이미 사용중인 이메일/닉네임 등)", HttpStatus.CONFLICT),
    DUPLICATE_LOGIN_ID("이미 사용 중인 로그인 아이디입니다.", HttpStatus.CONFLICT),
    DUPLICATE_EMAIL("이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT),
    DUPLICATE_NICKNAME("이미 사용 중인 닉네임입니다.", HttpStatus.CONFLICT),

    // 403 - 계정 상태
    USER_SUSPENDED("계정이 정지된 상태입니다.", HttpStatus.FORBIDDEN),

    // 404 - 조회
    USER_NOT_FOUND("유저를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    NICKNAME_HISTORY_NOT_FOUND("닉네임 변경 이력을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),

    // 401 - 인증
    INVALID_CREDENTIALS("로그인 정보가 올바르지 않습니다.", HttpStatus.UNAUTHORIZED),
    ;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    UserErrorCode(String message, HttpStatus httpStatus) {
        this.code = this.name();
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
