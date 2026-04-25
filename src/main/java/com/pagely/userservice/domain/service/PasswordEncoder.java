package com.pagely.userservice.domain.service;

/**
 * 비밀번호 해싱/검증 추상화. 도메인이 Spring Security 등의 인프라에 의존하지 않도록 인터페이스로 분리.
 */
public interface PasswordEncoder {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String hashedValue);
}
