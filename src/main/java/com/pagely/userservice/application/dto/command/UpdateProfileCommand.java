package com.pagely.userservice.application.dto.command;

import com.pagely.userservice.domain.model.Gender;
import java.time.LocalDate;

public record UpdateProfileCommand(
        String email,
        String name,
        String phone,
        Gender gender,
        LocalDate birthDate,
        String nickname
) {
}
