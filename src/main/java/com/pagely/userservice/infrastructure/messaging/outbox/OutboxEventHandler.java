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
 * <p><b>구독 정책</b></p>
 * 구체 이벤트 타입별로 메서드 분리:
 * <ul>
 *   <li>새 이벤트 추가 시 새 메서드 추가 (컴파일러가 타입 검증)</li>
 *   <li>미지원 이벤트는 자동으로 무시 (도메인 트랜잭션 영향 X)</li>
 *   <li>BEFORE_COMMIT 이라 도메인 트랜잭션과 원자성 보장</li>
 * </ul>
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
    public void onUserCreated(UserCreatedEvent event) {
        saveOutbox(event, UserCreatedEvent.TOPIC);
    }

    /**
     * 공통 처리 로직 — 직렬화 + Outbox 저장.
     */
    private void saveOutbox(BaseEvent event, String topic) {
        try {
            String payload = objectMapper.writeValueAsString(event);
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
}
