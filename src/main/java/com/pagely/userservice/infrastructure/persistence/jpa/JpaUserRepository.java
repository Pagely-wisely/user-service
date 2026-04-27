package com.pagely.userservice.infrastructure.persistence.jpa;

import com.pagely.userservice.domain.model.User;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * User 도메인 저장소.
 *
 * <p>기본 CRUD는 JpaRepository가 제공.
 * 도메인 특화 조회 메서드는 회원가입/로그인 이슈에서 확장.</p>
 *
 * <p>Soft delete 필터링(deleted_at IS NULL)은
 * 다음 이슈에서 @Where 또는 커스텀 쿼리로 적용 예정.</p>
 */
public interface JpaUserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByLoginId(String loginId);

    Optional<User> findByEmail(String email);

    boolean existsByLoginId(String loginId);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);
}
