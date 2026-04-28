package com.pagely.userservice.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT 관련 설정 매핑.
 *
 * @param secret                  JWT 서명용 비밀키 (HS256, 32바이트 이상)
 * @param accessTokenExpirationMs AccessToken 만료 시간 (밀리초)
 */
@ConfigurationProperties(prefix = "jwt") // ServiceApplication - @EnableConfigurationProperties(JwtProperties.class)
public record JwtProperties(
        String secret,
        long accessTokenExpirationMs
) {
}
