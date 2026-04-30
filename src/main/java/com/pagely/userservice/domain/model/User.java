package com.pagely.userservice.domain.model;

import com.pagely.common.auth.Role;
import com.pagely.common.entity.BaseEntity;
import com.pagely.userservice.domain.model.vo.Password;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Persistable;


@Entity
@Getter
@Table(name = "p_users")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity implements Persistable<UUID> {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "login_id", nullable = false, length = 50)
    private String loginId;

    @Embedded
    private Password password;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "nickname", nullable = false, length = 30)
    private String nickname;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false, length = 10)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "rating", nullable = false)
    private int rating;

    @Column(name = "is_suspended", nullable = false)
    private boolean isSuspended;

    // ====================================================================
    // 팩토리 메서드
    // ====================================================================
    // TODO: 실명이름, 전화번호, 성별, 생년월일 등은 별도 인증 검증을 적용한다.

    /**
     * 신규 유저 생성.
     *
     * @param hashedPassword BCrypt 등으로 해싱된 비밀번호 (평문 금지)
     * @return 새 User (ID 자동 생성, role=USER, rating=1000, isSuspended=false)
     */
    public static User create(
            String loginId,
            String email,
            Password hashedPassword,
            String name,
            String nickname,
            String phone,
            Gender gender,
            LocalDate birthDate
    ) {
        User user = new User();
        user.id = UUID.randomUUID();
        user.loginId = loginId;
        user.email = email;
        user.password = hashedPassword;
        user.name = name;
        user.role = Role.USER;
        user.nickname = nickname;
        user.phone = phone;
        user.gender = gender;
        user.birthDate = birthDate;
        user.rating = 1000;
        user.isSuspended = false;

        user.createdBy = user.id;
        user.updatedBy = user.id;
        return user;
    }

    /**
     * 일반유저 본인 프로필 수정 (본인이 직접 하는 경우)
     */
    public void updateMyProfile(
            String email,
            String name,
            String phone,
            Gender gender,
            LocalDate birthDate) {
        this.email = email;
        this.name = name;
        this.phone = phone;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    /**
     * 닉네임은 변경 시점부터 30일 제한과 이력 생성이 수반되는 '정책 필드'이기 때문에,
     * <p>서비스 레이어에서 명확하게 변경 여부를 대조한 뒤 전용 메서드(changeNickname)를 호출
     */
    public void changeNickname(String newNickname) {
        if (this.nickname.equals(newNickname)) {
            return;
        }
        this.nickname = newNickname;
    }


    /**
     * 관리자 수정 (관리자가 정책을 뛰어넘어 수정하는 경우)
     */
    public void adminUpdate(String email,
                            String name,
                            Role role,
                            String nickname,
                            String phone,
                            Gender gender,
                            LocalDate birthDate,
                            Integer rating,
                            boolean isSuspended) {
        this.email = email;
        this.name = name;
        this.role = role;
        this.nickname = nickname;
        this.phone = phone;
        this.gender = gender;
        this.birthDate = birthDate;
        this.rating = rating;
        this.isSuspended = isSuspended;
    }

    // ====================================================================
    // Persistable 구현
    // ====================================================================

    /**
     * Spring Data JPA가 신규 vs 기존을 판정. createdAt이 null이면 아직 영속화되지 않은 신규 엔티티 → INSERT. createdAt이 있으면 이미 영속화된 기존 엔티티 →
     * UPDATE.
     *
     * <p>id가 항상 미리 생성되는 본 엔티티의 특성상,
     * 기본 isNew 판정(id null 검사)을 그대로 쓸 수 없어 직접 구현.</p>
     */
    @Override
    public boolean isNew() {
        return getCreatedAt() == null;
    }
}
