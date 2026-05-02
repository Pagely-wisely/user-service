package com.pagely.userservice.infrastructure.messaging.outbox;

import com.pagely.userservice.infrastructure.persistence.jpa.JpaOutboxRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryAdapter implements OutboxRepository {

    private final JpaOutboxRepository jpaRepository;

    @Override
    public OutboxEvent save(OutboxEvent event) {
        return jpaRepository.save(event);
    }

    @Override
    public List<OutboxEvent> findUnpublished(Pageable pageable) {
        return jpaRepository.findUnpublished(pageable);
    }

    @Override
    public List<OutboxEvent> findFailedExceedingThreshold(int threshold, Pageable pageable) {
        return jpaRepository.findFailedExceedingThreshold(threshold, pageable);
    }
}
