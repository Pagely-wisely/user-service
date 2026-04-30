package com.pagely.userservice.application.dto.command;

import com.pagely.common.auth.Role;
import com.pagely.userservice.domain.model.Gender;
import java.time.LocalDate;

public record UpdateInfoCommand(
        String email,
        String name,
        Role role,
        String nickname,
        String phone,
        Gender gender,
        LocalDate birthDate,
        Integer rating,
        Boolean isSuspended
) {
}
