package com.pagely.userservice.infrastructure.config;

import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.lang.NonNull;

/**
 * UserContext(ThreadLocal)에서 유저 UUID를 추출하여 JPA Auditing에 공급한다.
 */
@Slf4j
public class UserContextAuditorAware implements AuditorAware<UUID> {

    @NonNull
    @Override
    public Optional<UUID> getCurrentAuditor() {
        // Filter에서 이미 가공해둔 UserContextHolder(또는 UserContext)를 참조합니다.
        UUID userId = null; // TODO: 임시 구현
        // UUID userId = UserContext.getUserId();

        if (userId == null) {
            // 회원가입 등 인증 정보가 없는 경우
            return Optional.empty();
        }

        return Optional.of(userId);
    }
}
