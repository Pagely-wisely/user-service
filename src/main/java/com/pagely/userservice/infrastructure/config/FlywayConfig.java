package com.pagely.userservice.infrastructure.config;

import com.pagely.userservice.infrastructure.security.BCryptPasswordEncoder;
import java.util.Map;
import org.springframework.boot.autoconfigure.flyway.FlywayConfigurationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {
    @Bean
    public FlywayConfigurationCustomizer flywayCustomizer(
            AdminProperties adminProps) {
        return configuration -> {
            // 사양에 따라 알고리즘 선택 가능 (BCrypt, Argon2 등)
            String hashed = new BCryptPasswordEncoder().encode(adminProps.password());
            configuration.placeholders(Map.of(
                    "adminId", adminProps.id(),
                    "encodedPassword", hashed));
        };

    }
}
