package com.pagely.userservice.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_user_nickname_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserNicknameHistory {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "old_nickname", nullable = false, length = 30)
    private String oldNickname;

    @Column(name = "new_nickname", nullable = false, length = 30)
    private String newNickname;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    @Column(name = "changed_by", nullable = false)
    private UUID changedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", length = 50)
    private NicknameChangeReason reason;

    public static UserNicknameHistory of(
            UUID userId,
            String oldNickname,
            String newNickname,
            UUID changedBy,
            NicknameChangeReason reason
    ) {
        UserNicknameHistory history = new UserNicknameHistory();
        history.id = UUID.randomUUID();
        history.userId = userId;
        history.oldNickname = oldNickname;
        history.newNickname = newNickname;
        history.changedAt = LocalDateTime.now();
        history.changedBy = changedBy;
        history.reason = reason;
        return history;
    }
}
