package com.pagely.userservice.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 서버 최초 실행시 만들어지는 Master계정 관련 설정
 */
@ConfigurationProperties(prefix = "init.admin")
public record AdminProperties(
        String id,
        String password
) {
}
