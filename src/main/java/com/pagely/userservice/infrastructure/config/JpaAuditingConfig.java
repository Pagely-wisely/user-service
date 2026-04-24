package com.pagely.userservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA에게 "누가 이 데이터를 만들었는지 물어볼 곳"을 알려주는 설정.
 * <p>
 * BaseEntity/BaseUserEntity의 @CreatedDate, @LastModifiedDate, @CreatedBy,@LastModifiedBy 자동 주입을 활성화한다.
 */
@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware") // JPA Auditing 기능을 켜기 (auditorAware Bean 사용)
public class JpaAuditingConfig {

    @Bean
    public AuditorAware<java.util.UUID> auditorAware() {
        return new UserContextAuditorAware();
    }
}
