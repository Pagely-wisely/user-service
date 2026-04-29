package com.pagely.userservice.presentation.dto.response;

import com.pagely.common.auth.Role;
import com.pagely.userservice.domain.model.Gender;
import com.pagely.userservice.domain.model.User;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 회원가입 응답 DTO.
 *
 * <p>비밀번호는 의도적으로 제외 (DTO 필드 자체 없음).</p>
 */
public record SignupResponse(
        UUID id,
        String loginId,
        String email,
        String name,
        String nickname,
        Role role,
        Gender gender,
        LocalDate birthDate,
        Integer rating,
        LocalDateTime createdAt
) {

    /**
     * User 엔티티 → SignupResponse 변환.
     */
    public static SignupResponse from(User user) {
        return new SignupResponse(
                user.getId(),
                user.getLoginId(),
                user.getEmail(),
                user.getName(),
                user.getNickname(),
                user.getRole(),
                user.getGender(),
                user.getBirthDate(),
                user.getRating(),
                user.getCreatedAt()
        );
    }
}
