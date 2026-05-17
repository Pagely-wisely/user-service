package com.pagely.userservice.presentation.dto.response;

import com.pagely.common.auth.Role;
import java.util.UUID;

/**
 * 자격 검증 성공 시 응답.
 *
 * <p>Auth Service 가 이 정보 기반으로 JWT 발급.</p>
 */
public record CredentialsVerificationResponse(
        UUID userId,
        Role role
) {

    public static CredentialsVerificationResponse of(UUID userId, Role role) {
        return new CredentialsVerificationResponse(userId, role);
    }
}
