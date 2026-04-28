package com.pagely.userservice.infrastructure.security;

import com.pagely.userservice.domain.model.Role;
import java.util.UUID;

/**
 * 현재 요청을 처리 중인 인증된 유저의 컨텍스트 정보.
 * <p>
 * Gateway 가 주입한 헤더(X-User-Id, X-User-Role)에서 추출.
 * </p>
 */
public record UserContext(
        UUID userId,
        Role role
) {

    public static UserContext of(UUID userId, Role role) {
        if (userId == null) {
            throw new IllegalArgumentException("userId 는 null 일 수 없습니다.");
        }
        if (role == null) {
            throw new IllegalArgumentException("role 은 null 일 수 없습니다.");
        }
        return new UserContext(userId, role);
    }
}
