package com.pagely.userservice.domain.repository;

import com.pagely.userservice.application.dto.query.UserSearchCondition;
import com.pagely.userservice.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserQueryRepository {

    Optional<User> findById(UUID id);

    Page<User> findAll(UserSearchCondition condition, Pageable pageable);

    // MSA 내부 서비스가 여러 사용자를 한 번에 조회할 때
    List<User> findAllByIds(List<UUID> ids);
}
