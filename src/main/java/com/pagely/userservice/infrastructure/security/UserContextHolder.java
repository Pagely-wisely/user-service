package com.pagely.userservice.infrastructure.security;

import com.pagely.common.exception.BusinessException;
import com.pagely.common.exception.CommonErrorCode;
import com.pagely.userservice.domain.model.Role;
import java.util.Optional;
import java.util.UUID;

/**
 * ThreadLocal 기반 인증 컨텍스트 홀더.
 *
 * <p>Gateway 가 주입한 인증 정보를 요청 처리 중 어디서든 접근 가능.
 * AuthContextFilter 가 요청 시작 시 set, 응답 후 clear.</p>
 *
 * <p><b>주의</b></p>
 * <ul>
 *   <li>호출 가능 영역: HTTP 요청 처리 스레드 내부 (필터/컨트롤러/서비스)</li>
 *   <li>cleanup 누락 시 스레드 풀 환경에서 권한 누수 발생 가능 (예: 다른 사용자의 정보가 보임)</li>
 * </ul>
 */
public final class UserContextHolder {

    private static final ThreadLocal<UserContext> CONTEXT = new ThreadLocal<>();

    private UserContextHolder() {
        throw new UnsupportedOperationException("Utility class");
    }

    // ── set / clear ────────────────────────────────────────────────────

    public static void set(UserContext context) {
        CONTEXT.set(context);
    }

    public static void clear() {
        CONTEXT.remove();
    }

    // ── UserContext ──────────────────────────────────────────────────

    public static UserContext get() {
        return CONTEXT.get();
    }

    public static Optional<UserContext> find() {
        return Optional.ofNullable(CONTEXT.get());
    }

    public static UserContext getOrThrow() {
        UserContext context = CONTEXT.get();
        if (context == null) {
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }
        return context;
    }

    // ── userId ─────────────────────────────────────────────────────────

    public static UUID getCurrentUserId() {
        UserContext context = CONTEXT.get();
        return context == null ? null : context.userId();
    }

    public static Optional<UUID> findCurrentUserId() {
        return find().map(UserContext::userId);
    }

    public static UUID getCurrentUserIdOrThrow() {
        return getOrThrow().userId();
    }

    // ── role ───────────────────────────────────────────────────────────

    public static Role getCurrentRole() {
        UserContext context = CONTEXT.get();
        return context == null ? null : context.role();
    }

    public static Optional<Role> findCurrentRole() {
        return find().map(UserContext::role);
    }

    public static Role getCurrentRoleOrThrow() {
        return getOrThrow().role();
    }

    // ── 인증 여부 ──────────────────────────────────────────────────────

    public static boolean isAuthenticated() {
        return CONTEXT.get() != null;
    }
}
