package com.pagely.userservice.infrastructure.security;

import com.pagely.userservice.domain.service.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 도메인 PasswordEncoder 인터페이스의 BCrypt 구현체.
 *
 * <p><b>BCrypt 설정</b></p>
 * <ul>
 *   <li>strength = 12 (해싱 라운드 2^12 = 4096회)</li>
 *   <li>적당한 보안 + 성능 균형 (10이 기본, 12 권장, 15 이상은 응답 지연)</li>
 * </ul>
 */

@Component
public class BCryptPasswordEncoder implements PasswordEncoder {

    private static final int BCRYPT_STRENGTH = 12;

    private final org.springframework.security.crypto.password.PasswordEncoder delegate
            = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder(BCRYPT_STRENGTH);

    @Override
    public String encode(String rawPassword) {
        return delegate.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String hashedValue) {
        return delegate.matches(rawPassword, hashedValue);
    }
}
