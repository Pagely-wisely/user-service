package com.pagely.userservice.infrastructure.security;

import com.pagely.common.exception.BusinessException;
import com.pagely.common.exception.CommonErrorCode;
import com.pagely.userservice.domain.model.Role;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * {@link AuthRequired} 어노테이션 처리 Aspect.
 *
 * <p>메서드 호출 전 UserContextHolder 검사:
 * <ul>
 *   <li>인증 정보 부재 → 401 UNAUTHORIZED</li>
 *   <li>role 미일치 → 403 FORBIDDEN</li>
 *   <li>통과 → 메서드 진행</li>
 * </ul>
 */
@Slf4j
@Aspect
@Component
public class AuthAspect {

    @Around("@annotation(authRequired)")
    public Object checkAuth(ProceedingJoinPoint pjp, AuthRequired authRequired) throws Throwable {
        // 1. 인증 검사
        if (!UserContextHolder.isAuthenticated()) {
            log.debug("인증 거부: 미인증 상태에서 보호 메서드 호출 — {}",
                    pjp.getSignature().toShortString());
            throw new BusinessException(CommonErrorCode.UNAUTHORIZED);
        }

        // 2. 권한 검사
        Role[] requiredRoles = authRequired.role();
        if (requiredRoles.length > 0) {
            Role currentRole = UserContextHolder.getCurrentRoleOrThrow();
            if (!Arrays.asList(requiredRoles).contains(currentRole)) {
                log.warn("권한 거부: role={}, required={}, method={}",
                        currentRole, Arrays.toString(requiredRoles),
                        pjp.getSignature().toShortString());
                throw new BusinessException(CommonErrorCode.FORBIDDEN);
            }
        }

        // 3. 통과
        return pjp.proceed();
    }
}
