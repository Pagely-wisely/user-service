package com.pagely.userservice.infrastructure.messaging.outbox;

import java.util.List;
import org.springframework.data.domain.Pageable;

/**
 * Outbox 이벤트 영속성 인터페이스 (Port).
 *
 * <p>OutboxEventHandler 와 OutboxPoller 가 사용.</p>
 */
public interface OutboxRepository {

    /**
     * Outbox 이벤트 저장.
     */
    OutboxEvent save(OutboxEvent event);

    /**
     * 미발행 이벤트 조회 (created_at 오름차순).
     *
     * <p>Poller 가 batch 단위로 가져오기 위해 Pageable 사용.</p>
     */
    List<OutboxEvent> findUnpublished(Pageable pageable);

    /**
     * 발행 시도 횟수가 일정 임계 이상인 이벤트 (모니터링)
     */
    List<OutboxEvent> findFailedExceedingThreshold(int threshold, Pageable pageable);
}
