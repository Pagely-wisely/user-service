package com.pagely.userservice.infrastructure.messaging.outbox;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Outbox 미발행 이벤트를 Kafka 로 발행하는 스케줄러.
 *
 * <p><b>동작</b></p>
 * <ol>
 *   <li>5초마다 미발행 이벤트 BATCH_SIZE 개 조회</li>
 *   <li>배치 내 이벤트를 동시에 Kafka 로 발행 (비동기 send)</li>
 *   <li>모든 발행 결과를 트랜잭션 안에서 확인</li>
 *   <li>성공 → published=true 마킹</li>
 *   <li>실패 → failure_count++, 다음 사이클에서 재시도</li>
 * </ol>
 *
 * <p><b>메시지 키 = aggregate_id (UUID 문자열)</b></p>
 * 같은 entity 의 이벤트는 같은 파티션으로 라우팅되어 순서가 보장됩니다.
 *
 * <p><b>주의</b></p>
 * PageRequest 는 반드시 {@code org.springframework.data.domain.PageRequest} 를 사용해야 합니다.
 * 공통 모듈의 PageRequest 는 허용 사이즈(10/30/50)가 제한되어 BATCH_SIZE 가 무시됩니다.
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
                PageRequest.of(0, BATCH_SIZE)); // import 주의! 공통모듈 사용 X
        if (events.isEmpty()) {
            return;
        }

        log.debug("Outbox 발행 시작: count={}", events.size());

        List<CompletableFuture<SendResult<String, String>>> futures = events.stream()
                .map(event -> kafkaTemplate.send(
                        event.getTopic(),
                        event.getAggregateId().toString(),
                        event.getPayload()
                ).toCompletableFuture())
                .toList();

        for (int i = 0; i < events.size(); i++) {
            OutboxEvent event = events.get(i);
            try {
                futures.get(i).get(SEND_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                event.markPublished();   // 트랜잭션 안에서 실행 ✅
            } catch (Exception e) {
                event.recordFailure(e.getMessage());
                log.error("Outbox 발행 실패: outboxId={}, eventType={}, failureCount={}",
                        event.getId(), event.getEventType(), event.getFailureCount(), e);
            }
        }

        log.debug("Outbox 발행 완료: count={}", events.size());
    }
}
