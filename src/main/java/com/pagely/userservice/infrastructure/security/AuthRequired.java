package com.pagely.userservice.infrastructure.security;

import com.pagely.userservice.domain.model.Role;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 메서드 호출 전 인증/권한 검사를 강제하는 어노테이션.
 *
 * <p><b>사용 예</b></p>
 * <pre>{@code
 * // 인증만 필수, role 무관
 * @AuthRequired
 * @GetMapping("/me")
 * public UserResponse getMyInfo() { ... }
 *
 * // 특정 role 만 허용
 * @AuthRequired(role = Role.MASTER)
 * @DeleteMapping("/users/{id}")
 * public void deleteUser(@PathVariable UUID id) { ... }
 *
 * // 여러 role 허용
 * @AuthRequired(role = {Role.MASTER, Role.CREATOR})
 * @PostMapping("/admin/...")
 * public void adminTask() { ... }
 * }</pre>
 *
 * <p><b>동작</b></p>
 * <ul>
 *   <li>UserContextHolder 에 인증 정보가 없으면 BusinessException(UNAUTHORIZED) → 401</li>
 *   <li>role 지정 시 현재 role 이 허용 목록에 없으면 BusinessException(FORBIDDEN) → 403</li>
 * </ul>
 *
 * @see AuthAspect
 */
@Target(ElementType.METHOD) // 어노테이션을 어디에 붙일 수 있는지 제한 (메서드에만)
@Retention(RetentionPolicy.RUNTIME) //
public @interface AuthRequired {

    /**
     * 허용할 role. 빈 배열 = 인증만 검사 (role 무관).
     */
    Role[] role() default {};
}
