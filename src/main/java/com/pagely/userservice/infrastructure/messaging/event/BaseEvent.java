package com.pagely.userservice.infrastructure.messaging.event;

import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

/**
 * 도메인 이벤트의 공통 메타 정보.
 * <p>
 * 향후 pagely-common 으로 이관 (예정)</p>
 *
 * <p><b>설계 의도</b></p>
 * <ul>
 *   <li>{@code eventId}: 이벤트 고유 식별자 (Consumer idempotency 용). UUID.toString().</li>
 *   <li>{@code eventType}: 서브클래스명 자동 (예: "UserCreatedEvent").</li>
 *   <li>{@code domainType}: 도메인 명명 (예: "USER"). aggregate type.</li>
 *   <li>{@code domainId}: 도메인 엔티티 ID (예: userId). Kafka 메시지 키로 활용.</li>
 *   <li>{@code occurredAt}: Instant (UTC 기준) — 타임존 모호성 제거.</li>
 *   <li>{@code payload}: 이벤트 본문 (도메인 데이터).</li>
 * </ul>
 *
 * <p>String / Instant 사용 — 다른 언어 Consumer 호환.</p>
 */
@Getter
public abstract class BaseEvent {

    private final String eventId;
    private final String eventType;
    private final String domainType;
    private final String domainId;
    private final Instant occurredAt;
    private final Object payload;

    /**
     * UUID 도메인 ID (대부분의 도메인 케이스).
     */
    protected BaseEvent(String domainType, UUID domainId, Object payload) {
        this(domainType, domainId == null ? null : domainId.toString(), payload);
    }

    /**
     * domainId 가 없는 케이스 (예: 시스템 이벤트).
     */
    protected BaseEvent(String domainType, Object payload) {
        this(domainType, (String) null, payload);
    }

    /**
     * 모든 생성자가 위임하는 메인 생성자.
     */
    protected BaseEvent(String domainType, String domainId, Object payload) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = this.getClass().getSimpleName();
        this.domainType = domainType;
        this.domainId = domainId;
        this.occurredAt = Instant.now();
        this.payload = payload;
    }
}
