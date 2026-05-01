package com.pagely.userservice.infrastructure.messaging.event;

import com.pagely.common.auth.Role;
import com.pagely.userservice.domain.model.User;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 회원가입 완료 이벤트.
 *
 * <p>발행 위치: {@link com.pagely.userservice.application.service.UserApplicationService#signup}</p>
 * <p>토픽: {@code user.created}</p>
 *
 * <p>다른 서비스의 Consumer 가 이 페이로드 스키마에 맞춰 ObjectMapper 매핑.</p>
 */
public class UserCreatedEvent extends BaseEvent {

    public static final String DOMAIN_TYPE = "USER";
    public static final String TOPIC = "user.created";

    private UserCreatedEvent(UUID userId, Payload payload) {
        super(DOMAIN_TYPE, userId, payload);
    }

    /**
     * User 엔티티에서 이벤트 생성. 전화번호, 생일, 성별, 평점 등 민감 정보 제외 후 페이로드 구성.
     */
    public static UserCreatedEvent from(User user) {
        Payload payload = new Payload(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
                user.getNickname(),
                user.getRole(),
                user.getCreatedAt()
        );
        return new UserCreatedEvent(user.getId(), payload);
    }

    /**
     * 이벤트 페이로드 — 다른 서비스 Consumer 가 매핑
     */
    public record Payload(
            UUID userId,
            String loginId,
            String email,
            String nickname,
            Role role,
            LocalDateTime createdAt
    ) {
    }
}
