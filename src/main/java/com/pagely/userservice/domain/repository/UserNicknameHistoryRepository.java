package com.pagely.userservice.domain.repository;

import com.pagely.userservice.domain.model.UserNicknameHistory;
import java.util.Optional;
import java.util.UUID;

public interface UserNicknameHistoryRepository {

    UserNicknameHistory save(UserNicknameHistory history);

    /**
     * 특정 유저의 가장 최근 닉네임 변경 이력을 조회. 서비스 레이어에서 30일 제한(validateNickname30Days)을 체크할 때 사용합니다.
     * <p>단, 'CREATE', 'USER_CHANGE'와 같이 사용자에 의한 변경 사유만 포함합니다.
     */
    Optional<UserNicknameHistory> findFirstByUserIdOrderByChangedAtDesc(UUID userId);
}
