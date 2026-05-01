package com.pagely.userservice.infrastructure.persistence;

import com.pagely.userservice.domain.model.NicknameChangeReason;
import com.pagely.userservice.domain.model.UserNicknameHistory;
import com.pagely.userservice.domain.repository.UserNicknameHistoryRepository;
import com.pagely.userservice.infrastructure.persistence.jpa.JpaUserNicknameHistoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserNicknameHistoryRepositoryAdapter implements UserNicknameHistoryRepository {

    private final JpaUserNicknameHistoryRepository jpaRepository;

    private static final List<NicknameChangeReason> SELF_CHANGE_REASONS =
            List.of(NicknameChangeReason.CREATE, NicknameChangeReason.USER_CHANGE);

    @Override
    public UserNicknameHistory save(UserNicknameHistory history) {
        return jpaRepository.save(history);
    }

    @Override
    public Optional<UserNicknameHistory> findFirstByUserIdOrderByChangedAtDesc(UUID userId) {
        return jpaRepository.findFirstByUserIdAndReasonInOrderByChangedAtDesc(userId, SELF_CHANGE_REASONS);
    }
}
