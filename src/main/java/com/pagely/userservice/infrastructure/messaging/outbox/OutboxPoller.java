package com.pagely.userservice.infrastructure.messaging.outbox;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Outbox 미발행 이벤트를 Kafka 로 발행하는 스케줄러.
 *
 * <p><b>동작</b></p>
 * <ol>
 *   <li>5초마다 미발행 이벤트 BATCH_SIZE 개 조회</li>
 *   <li>Kafka 로 발행</li>
 *   <li>성공 → published=true 마킹</li>
 *   <li>실패 → failure_count++, 다음 사이클에서 재시도</li>
 * </ol>
 *
 * <p><b>메시지 키 = aggregate_id (UUID 문자열)</b></p>
 * 같은 entity 의 이벤트는 같은 파티션 → 순서 보장.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPoller {

    private static final int BATCH_SIZE = 100;
    private static final long SEND_TIMEOUT_SECONDS = 10;

    private final OutboxRepository outboxRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms:5000}")
    @Transactional
    public void publishPendingEvents() {
        List<OutboxEvent> events = outboxRepository.findUnpublished(
                PageRequest.of(0, BATCH_SIZE)
        );

        if (events.isEmpty()) {
            return;
        }

        log.debug("Outbox 발행 시작: count={}", events.size());

        for (OutboxEvent event : events) {
            try {
                publishToKafka(event);
                event.markPublished();
            } catch (Exception e) {
                event.recordFailure(e.getMessage());
                log.error("Outbox 발행 실패: outboxId={}, eventType={}, failureCount={}",
                        event.getId(), event.getEventType(), event.getFailureCount(), e);
            }
        }

        log.debug("Outbox 발행 완료: count={}", events.size());
    }

    private void publishToKafka(OutboxEvent event) throws ExecutionException, InterruptedException, TimeoutException {
        String topic = event.getTopic();
        String messageKey = event.getAggregateId().toString();
        String payload = event.getPayload();

        // 동기 발행 — 실패 시 즉시 catch 가능 (성능보다 신뢰성 우선)
        kafkaTemplate.send(topic, messageKey, payload).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);

        log.debug("Kafka 발행 성공: topic={}, key={}, eventType={}",
                topic, messageKey, event.getEventType());
    }
}
