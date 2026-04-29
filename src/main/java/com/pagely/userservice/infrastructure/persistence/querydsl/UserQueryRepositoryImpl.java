package com.pagely.userservice.infrastructure.persistence.querydsl;

import com.pagely.common.auth.Role;
import com.pagely.userservice.application.dto.query.UserSearchCondition;
import com.pagely.userservice.domain.model.Gender;
import com.pagely.userservice.domain.model.QUser;
import com.pagely.userservice.domain.model.User;
import com.pagely.userservice.domain.repository.UserQueryRepository;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

@Repository
@RequiredArgsConstructor
public class UserQueryRepositoryImpl implements UserQueryRepository {

    private final JPAQueryFactory queryFactory; // QueryDSL이 제공하는 쿼리 생성 빌더
    private static final QUser user = QUser.user; // APT가 생성한 Q클래스

    // ================================================================
    // 단건 조회
    // ================================================================

    @Override
    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(user)
                        .where(
                                user.id.eq(id),
                                isNotDeleted()
                        )
                        .fetchOne()
        );
    }

    // ================================================================
    // 다건 조회 (동적 조건 + 페이징)
    // ================================================================

    @Override
    public Page<User> findAll(UserSearchCondition cond, Pageable pageable) {
        // 1. 데이터 조회
        List<User> content = queryFactory
                .selectFrom(user)
                .where(
                        idsIn(cond.getIds()),
                        loginIdEq(cond.getLoginId()),
                        nameContains(cond.getName()),
                        emailEq(cond.getEmail()),
                        nicknameContains(cond.getNickname()),
                        roleEq(cond.getRole()),
                        genderEq(cond.getGender()),
                        isSuspendedEq(cond.getIsSuspended()),
                        keywordSearch(cond.getKeyword()),
                        createdAtBetween(cond.getCreatedAtFrom(), cond.getCreatedAtTo()),  // 추가
                        updatedAtBetween(cond.getUpdatedAtFrom(), cond.getUpdatedAtTo()),  // 추가
                        createdByEq(cond.getCreatedBy()),                                  // 추가
                        updatedByEq(cond.getUpdatedBy()),                                  // 추가
                        isNotDeleted()
                )
                .orderBy(user.createdAt.desc())
                .offset(pageable.getOffset())   // (page * size) 만큼 건너뜀
                .limit(pageable.getPageSize())  // 최대 size개 가져옴
                .fetch();

        // 2. 카운트 쿼리 (페이지 총 개수 계산용, select/order 없이 조건만)
        Long total = queryFactory
                .select(user.count())
                .from(user)
                .where(
                        idsIn(cond.getIds()),
                        loginIdEq(cond.getLoginId()),
                        nameContains(cond.getName()),
                        emailEq(cond.getEmail()),
                        nicknameContains(cond.getNickname()),
                        roleEq(cond.getRole()),
                        genderEq(cond.getGender()),
                        isSuspendedEq(cond.getIsSuspended()),
                        keywordSearch(cond.getKeyword()),
                        isNotDeleted()
                )
                .fetchOne();

        return new PageImpl<>(content, pageable, total == null ? 0 : total);
    }

    // ================================================================
    // 배치 조회 (MSA 내부 호출용)
    // ================================================================

    @Override
    public List<User> findAllByIds(List<UUID> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return List.of();
        }
        return queryFactory
                .selectFrom(user)
                .where(
                        user.id.in(ids),
                        isNotDeleted()
                )
                .fetch();
    }

    // ================================================================
    // 조건 메서드 - BooleanExpression
    // null 반환 시 QueryDSL이 해당 where 조건을 자동으로 무시하게 된다.
    // ================================================================

    private BooleanExpression idsIn(List<UUID> ids) {
        return CollectionUtils.isEmpty(ids) ? null : user.id.in(ids);
    }

    private BooleanExpression loginIdEq(String loginId) {
        return StringUtils.hasText(loginId) ? user.loginId.eq(loginId) : null;
    }

    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ? user.name.contains(name) : null;
    }

    private BooleanExpression emailEq(String email) {
        return StringUtils.hasText(email) ? user.email.eq(email) : null;
    }

    private BooleanExpression nicknameContains(String nickname) {
        return StringUtils.hasText(nickname) ? user.nickname.contains(nickname) : null;
    }

    private BooleanExpression roleEq(Role role) {
        return role != null ? user.role.eq(role) : null;
    }

    private BooleanExpression genderEq(Gender gender) {
        return gender != null ? user.gender.eq(gender) : null;
    }

    private BooleanExpression isSuspendedEq(Boolean isSuspended) {
        return isSuspended != null ? user.isSuspended.eq(isSuspended) : null;
    }

    // name OR email OR nickname 중 하나라도 포함되면 매칭
    private BooleanExpression keywordSearch(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }
        return user.name.containsIgnoreCase(keyword)
                .or(user.email.containsIgnoreCase(keyword))
                .or(user.nickname.containsIgnoreCase(keyword));
    }

    // 소프트 삭제 필터링 (공통모듈 BaseEntity.deletedAt 참고)
    private BooleanExpression isNotDeleted() {
        return user.deletedAt.isNull();
    }

    // ── 감사 필드 조건 ──────────────────────────────────────────────────────

    private BooleanExpression createdAtBetween(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null) {
            return user.createdAt.between(from, to);
        }
        if (from != null) {
            return user.createdAt.goe(from); // greater or equal
        }
        if (to != null) {
            return user.createdAt.loe(to);   // less or equal
        }
        return null;
    }

    private BooleanExpression updatedAtBetween(LocalDateTime from, LocalDateTime to) {
        if (from != null && to != null) {
            return user.updatedAt.between(from, to);
        }
        if (from != null) {
            return user.updatedAt.goe(from);
        }
        if (to != null) {
            return user.updatedAt.loe(to);
        }
        return null;
    }

    private BooleanExpression createdByEq(UUID createdBy) {
        return createdBy != null ? user.createdBy.eq(createdBy) : null;
    }

    private BooleanExpression updatedByEq(UUID updatedBy) {
        return updatedBy != null ? user.updatedBy.eq(updatedBy) : null;
    }
}
