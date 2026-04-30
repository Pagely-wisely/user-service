package com.pagely.userservice.infrastructure.persistence;

import com.pagely.userservice.domain.model.UserNicknameHistory;
import com.pagely.userservice.domain.repository.UserNicknameHistoryRepository;
import com.pagely.userservice.infrastructure.persistence.jpa.JpaUserNicknameHistoryRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserNicknameHistoryRepositoryAdapter implements UserNicknameHistoryRepository {

    private final JpaUserNicknameHistoryRepository jpaRepository;

    @Override
    public UserNicknameHistory save(UserNicknameHistory history) {
        return jpaRepository.save(history);
    }

    @Override
    public Optional<UserNicknameHistory> findFirstByUserIdOrderByChangedAtDesc(UUID userId) {
        return jpaRepository.findFirstByUserIdOrderByChangedAtDesc(userId);
    }
}
