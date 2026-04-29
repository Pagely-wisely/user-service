package com.pagely.userservice.presentation.dto.response;

import com.pagely.common.auth.Role;
import com.pagely.userservice.domain.model.Gender;
import com.pagely.userservice.domain.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record UserInfoResponse(
        UUID id,
        String loginId,
        String name,
        String nickname,
        String email,
        String phone,
        Role role,
        Gender gender,
        LocalDate birthDate,
        Integer rating,
        Boolean isSuspended,
        LocalDateTime createdAt
) {
    public static UserInfoResponse from(User user) {
        return new UserInfoResponse(
                user.getId(),
                user.getLoginId(),
                user.getName(),
                user.getNickname(),
                user.getEmail(),
                user.getPhone(),
                user.getRole(),
                user.getGender(),
                user.getBirthDate(),
                user.getRating(),
                user.getIsSuspended(),
                user.getCreatedAt()
        );
    }
}
