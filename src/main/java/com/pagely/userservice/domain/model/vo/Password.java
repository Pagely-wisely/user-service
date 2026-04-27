package com.pagely.userservice.domain.model.vo;

import com.pagely.common.exception.BusinessException;
import com.pagely.userservice.domain.exception.UserErrorCode;
import com.pagely.userservice.domain.service.PasswordEncoder;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 해시된 비밀번호를 표현하는 Value Object.
 */
@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Password {

    @Column(name = "password", nullable = false, length = 255)
    private String hashedValue;

    private Password(String hashedValue) {
        this.hashedValue = hashedValue;
    }

    /**
     * 평문을 해싱해서 Password 객체 생성.
     *
     * @param rawPassword 평문 (정책 검증 후 즉시 해싱됨)
     * @param encoder     해싱에 사용할 인코더 (BCrypt 등)
     */
    public static Password of(String rawPassword, PasswordEncoder encoder) {
        String normalized = validatePolicy(rawPassword);
        return new Password(encoder.encode(normalized));
    }

    /**
     * 이미 해싱된 값 (DB에서 읽어올 때) 두 번 해싱 방지
     */
    public static Password fromHashed(String hashedValue) {
        return new Password(hashedValue);
    }

    /**
     * 평문 비밀번호가 이 객체의 해시와 일치하는지 검증.
     */
    public boolean matches(String rawPassword, PasswordEncoder encoder) {
        return encoder.matches(rawPassword, this.hashedValue);
    }

    /**
     * 정책 검증 : NIST 가이드
     */
    private static String validatePolicy(String rawPassword) {
        if (rawPassword == null) {
            throw new BusinessException(UserErrorCode.PASSWORD_REQUIRED);
        }
        if (rawPassword.length() < 10 || rawPassword.length() > 64) {
            throw new BusinessException(UserErrorCode.PASSWORD_LENGTH_INVALID);
        }
        if (rawPassword.chars().anyMatch(Character::isWhitespace)) {
            throw new BusinessException(UserErrorCode.PASSWORD_WHITESPACE_NOT_ALLOWED);
        }
        return rawPassword;
    }

    // a.equals(b) (a가 null이면 NPE) -> Objects.equalse(a,b) 사용
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Password that)) {
            return false;
        }
        return Objects.equals(hashedValue, that.hashedValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hashedValue);
    }

    /**
     * 해시 값이라도 노출 안 함. (로컬 브루트포스 방지)
     */
    @Override
    public String toString() {
        return "Password{****}";
    }
}
