package com.pagely.userservice.infrastructure.persistence.jpa;

import com.pagely.userservice.infrastructure.messaging.outbox.OutboxEvent;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 * Spring Data JPA OutboxEvent Repository.
 *
 * <p>{@link com.pagely.userservice.infrastructure.messaging.outbox.OutboxRepositoryAdapter}
 * 가 이걸 위임 호출.</p>
 */
public interface JpaOutboxRepository extends JpaRepository<OutboxEvent, UUID> {

    /**
     * 미발행 이벤트 조회.
     *
     * <p>인덱스 idx_outbox_unpublished 적중을 위해 published=false + deleted_at IS NULL 조건.</p>
     */
    @Query("""
            SELECT e FROM OutboxEvent e
            WHERE e.published = false
              AND e.deletedAt IS NULL
            ORDER BY e.createdAt ASC
            """)
    List<OutboxEvent> findUnpublished(Pageable pageable);

    /**
     * 발행 실패 횟수가 임계값 이상인 이벤트 조회 (운영 모니터링).
     */
    @Query("""
            SELECT e FROM OutboxEvent e
            WHERE e.published = false
              AND e.failureCount >= :threshold
              AND e.deletedAt IS NULL
            ORDER BY e.lastFailureAt DESC
            """)
    List<OutboxEvent> findFailedExceedingThreshold(int threshold, Pageable pageable);
}
