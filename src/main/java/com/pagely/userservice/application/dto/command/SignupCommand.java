package com.pagely.userservice.application.dto.command;

import com.pagely.userservice.domain.model.Gender;
import java.time.LocalDate;

public record SignupCommand(
        String loginId,
        String email,
        String password,
        String name,
        String nickname,
        String phone,
        Gender gender,
        LocalDate birthDate
) {
}
