package com.pagely.userservice.application.service;

import com.pagely.common.exception.BusinessException;
import com.pagely.userservice.application.dto.query.UserSearchCondition;
import com.pagely.userservice.domain.exception.UserErrorCode;
import com.pagely.userservice.domain.model.User;
import com.pagely.userservice.domain.repository.UserQueryRepository;
import com.pagely.userservice.presentation.dto.response.AdminUserInfoResponse;
import com.pagely.userservice.presentation.dto.response.UserInfoResponse;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserQueryService {

    private final UserQueryRepository userQueryRepository;

    // 일반 단건 조회 - 기본 정보
    public UserInfoResponse getUser(UUID id) {
        User user = userQueryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return UserInfoResponse.from(user);
    }

    // 상세 단건 조회 - 감사(Audit) 정보 포함
    public AdminUserInfoResponse getUserDetail(UUID id) {
        User user = userQueryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));
        return AdminUserInfoResponse.from(user);
    }

    // 내부 MSA 배치 조회
    public List<UserInfoResponse> getUsersByIds(List<UUID> ids) {
        return userQueryRepository.findAllByIds(ids).stream()
                .map(UserInfoResponse::from)
                .toList();
    }

    // 목록 조회 (동적 조건 + 페이징)
    public Page<User> searchUsers(UserSearchCondition condition, Pageable pageable) {
        return userQueryRepository.findAll(condition, pageable);
    }
}
