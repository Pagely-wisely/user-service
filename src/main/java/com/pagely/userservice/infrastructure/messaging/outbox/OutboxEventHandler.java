package com.pagely.userservice.infrastructure.messaging.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pagely.userservice.infrastructure.messaging.event.BaseEvent;
import com.pagely.userservice.infrastructure.messaging.event.UserCreatedEvent;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 도메인 이벤트를 Outbox 테이블에 저장하는 핸들러.
 *
 * <p><b>동작 흐름</b></p>
 * <ol>
 *   <li>도메인 서비스가 ApplicationEventPublisher 로 이벤트 발행</li>
 *   <li>이 핸들러가 BEFORE_COMMIT 시점에 호출됨 → 같은 트랜잭션 안에서 처리</li>
 *   <li>JSON 직렬화 → 토픽 결정 → Outbox 저장</li>
 *   <li>도메인 트랜잭션 커밋 시 User + Outbox 함께 커밋 (원자성)</li>
 * </ol>
 *
 * <p>실제 Kafka 발행은 별도 OutboxPoller 가 담당 (Outbox 패턴).</p>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxEventHandler {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void onDomainEvent(BaseEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            String topic = resolveTopic(event);
            UUID aggregateId = UUID.fromString(event.getDomainId());

            OutboxEvent outbox = OutboxEvent.of(
                    event.getDomainType(),
                    aggregateId,
                    event.getEventType(),
                    topic,
                    payload
            );
            outboxRepository.save(outbox);

            log.debug("Outbox 저장 완료: eventType={}, domainId={}, outboxId={}",
                    event.getEventType(), event.getDomainId(), outbox.getId());
        } catch (JsonProcessingException e) {
            log.error("이벤트 직렬화 실패: eventType={}", event.getEventType(), e);
            throw new IllegalStateException("이벤트 직렬화 실패: " + event.getEventType(), e);
        }
    }

    /**
     * 이벤트 타입에 따른 Kafka 토픽 결정.
     */
    private String resolveTopic(BaseEvent event) {
        return switch (event.getEventType()) {
            case "UserCreatedEvent" -> UserCreatedEvent.TOPIC;
            // TODO: case "UserProfileUpdatedEvent" -> UserProfileUpdatedEvent.TOPIC;
            default -> throw new IllegalArgumentException(
                    "Unsupported event type: " + event.getEventType());
        };
    }
}
