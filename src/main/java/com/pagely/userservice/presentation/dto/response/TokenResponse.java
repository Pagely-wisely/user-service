package com.pagely.userservice.presentation.dto.response;

/**
 * 토큰 발급 응답 DTO.
 *
 * <p>OAuth 2.0 RFC 6749 의 token endpoint 응답 형식 참조.</p>
 *
 * @param accessToken 발급된 JWT
 * @param tokenType   토큰 타입 (항상 "Bearer")
 * @param expiresIn   만료까지 남은 시간 (초)
 */
public record TokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {

    public static TokenResponse of(String accessToken, long expiresInSeconds) {
        return new TokenResponse(accessToken, "Bearer", expiresInSeconds); // 토큰 타입을 고정
    }
}
