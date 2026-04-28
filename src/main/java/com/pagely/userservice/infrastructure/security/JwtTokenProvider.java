package com.pagely.userservice.infrastructure.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 생성/검증. Gateway와 UserService 양쪽이 동일한 jwt.secret 환경변수 공유.</p>
 *
 * <p><b>토큰 클레임</b></p>
 * <ul>
 *   <li>sub: 유저 UUID</li>
 *   <li>role: 유저 권한 (USER, MASTER, CREATOR)</li>
 *   <li>iat: 발급 시간</li>
 *   <li>exp: 만료 시간 (jwt.access-token-expiration-ms 기반)</li>
 * </ul>
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private static final String CLAIM_ROLE = "role";

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.secret().getBytes(StandardCharsets.UTF_8)
        );
        this.accessTokenExpirationMs = jwtProperties.accessTokenExpirationMs();
    }

    /**
     * AccessToken 생성.
     *
     * @param userId 유저 UUID (sub 클레임)
     * @param role   유저 권한 문자열 (role 클레임)
     * @return JWT 토큰 문자열
     */
    public String createAccessToken(UUID userId, String role) {
        long now = System.currentTimeMillis();
        Date issuedAt = new Date(now);
        Date expiration = new Date(now + accessTokenExpirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_ROLE, role)
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 만료 시간(초 단위) 반환. 응답 DTO 의 expiresIn 에 사용.
     */
    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationMs / 1000;
    }
}
