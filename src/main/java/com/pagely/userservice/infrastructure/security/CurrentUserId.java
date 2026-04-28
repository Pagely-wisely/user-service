package com.pagely.userservice.infrastructure.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 컨트롤러 메서드 파라미터에 현재 인증된 유저의 ID 를 자동 주입.
 *
 * <p><b>사용 예</b></p>
 * <pre>{@code
 * @GetMapping("/me")
 * public UserResponse getMyInfo(@CurrentUserId UUID userId) {
 *     return userService.findById(userId);
 * }
 * }</pre>
 *
 * <p>UserContextHolder 에 인증 정보가 없으면 BusinessException(UNAUTHORIZED).
 *
 * @AuthRequired 와 함께 쓰면 AOP 가 먼저 차단하여 더 명확.</p>
 * @see CurrentUserIdResolver
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUserId {
}
