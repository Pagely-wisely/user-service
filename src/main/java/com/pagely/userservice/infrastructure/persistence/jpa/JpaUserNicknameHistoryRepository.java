package com.pagely.userservice.infrastructure.persistence.jpa;

import com.pagely.userservice.domain.model.NicknameChangeReason;
import com.pagely.userservice.domain.model.UserNicknameHistory;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserNicknameHistoryRepository extends JpaRepository<UserNicknameHistory, UUID> {
    /**
     * 특정 유저의 이력 중 가장 최근(Desc) 1건(First)만 조회
     */
    Optional<UserNicknameHistory> findFirstByUserIdAndReasonInOrderByChangedAtDesc(UUID userId,
                                                                                   List<NicknameChangeReason> selfChangeReasons);

}
