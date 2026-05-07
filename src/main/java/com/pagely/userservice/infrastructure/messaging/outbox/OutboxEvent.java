package com.pagely.userservice.infrastructure.messaging.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Outbox 패턴의 이벤트 저장 엔티티.
 *
 * <p><b>생명주기</b></p>
 * <ol>
 *   <li>도메인 트랜잭션 안에서 {@link #of} 로 생성 + 저장 (published=false)</li>
 *   <li>OutboxPoller 가 미발행 이벤트 조회</li>
 *   <li>Kafka 발행 성공 시 {@link #markPublished} 호출 → published=true</li>
 *   <li>발행 실패 시 {@link #recordFailure} 호출 → failure_count++</li>
 * </ol>
 *
 * <p><b>id 와 aggregate_id 차이</b></p>
 * <ul>
 *   <li>id: Outbox 엔티티 자체의 PK (UUID 자동 생성)</li>
 *   <li>aggregate_id: 이벤트가 가리키는 도메인의 엔티티의 ID (예: User.id)</li>
 * </ul>
 */
@Entity
@Getter
@Table(name = "p_outbox")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "aggregate_type", nullable = false, length = 50)
    private String aggregateType;

    @Column(name = "aggregate_id", nullable = false, columnDefinition = "UUID")
    private UUID aggregateId;

    @Column(name = "event_type", nullable = false, length = 100)
    private String eventType;

    @Column(name = "topic", nullable = false, length = 100)
    private String topic;

    /**
     * JSON 직렬화된 이벤트 페이로드. Hibernate 6+ 가 String → JSONB 자동 매핑.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "JSONB")
    private String payload;

    @Column(name = "published", nullable = false)
    private boolean published;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "failure_count", nullable = false)
    private int failureCount;

    @Column(name = "last_failure_at")
    private LocalDateTime lastFailureAt;

    @Column(name = "last_failure_message", columnDefinition = "TEXT")
    private String lastFailureMessage;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    // ====================================================================
    // 팩토리 메서드
    // ====================================================================

    /**
     * 새 Outbox 이벤트 생성.
     */
    public static OutboxEvent of(
            String aggregateType,
            UUID aggregateId,
            String eventType,
            String topic,
            String payload
    ) {
        if (aggregateType == null || aggregateType.isBlank()) {
            throw new IllegalArgumentException("aggregateType 은 필수입니다.");
        }
        if (aggregateId == null) {
            throw new IllegalArgumentException("aggregateId 는 필수입니다.");
        }
        if (eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException("eventType 은 필수입니다.");
        }
        if (topic == null || topic.isBlank()) {
            throw new IllegalArgumentException("topic 은 필수입니다.");
        }
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("payload 는 필수입니다.");
        }

        OutboxEvent event = new OutboxEvent();
        event.aggregateType = aggregateType;
        event.aggregateId = aggregateId;
        event.eventType = eventType;
        event.topic = topic;
        event.payload = payload;
        event.published = false;
        event.failureCount = 0;
        return event;
    }

    // ====================================================================
    // 상태 변경
    // ====================================================================

    /**
     * 발행 성공 마킹.
     */
    public void markPublished() {
        this.published = true;
        this.publishedAt = LocalDateTime.now();
    }

    /**
     * 발행 실패 기록.
     */
    public void recordFailure(String message) {
        this.failureCount++;
        this.lastFailureAt = LocalDateTime.now();
        this.lastFailureMessage = truncateMessage(message);
    }

    private String truncateMessage(String message) {
        if (message == null) {
            return null;
        }
        // 저장할 때 DB 부하 방지, 읽어올 때 메모리 절약, DB허용 길이 초과시 데이터 저장 자체 실패를 미리 방지
        return message.length() > 2000 ? message.substring(0, 2000) : message;
    }

}
