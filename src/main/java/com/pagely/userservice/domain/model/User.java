package com.pagely.userservice.domain.model;

import com.pagely.userservice.domain.model.vo.Password;
import com.pagely.userservice.temp.entity.BaseEntity;
import jakarta.persistence.Column;
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

    @Column(name = "password", nullable = false, length = 255)
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
    private Integer rating;

    @Column(name = "is_suspended", nullable = false)
    private Boolean isSuspended;

    // ====================================================================
    // 팩토리 메서드
    // ====================================================================

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
        user.createdBy = user.id;
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
        return user;
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
