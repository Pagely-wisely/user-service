package com.pagely.userservice.presentation.dto.request;

import com.pagely.common.auth.Role;
import com.pagely.userservice.application.dto.command.UpdateInfoCommand;
import com.pagely.userservice.domain.model.Gender;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;

public record UpdateInfoRequest(
        @Email String email,
        String name,
        Role role,
        String nickname,
        String phone,
        Gender gender,
        LocalDate birthDate,
        Integer rating,
        Boolean isSuspended
) {
    public UpdateInfoCommand toCommand() {
        return new UpdateInfoCommand(
                email, name, role, nickname, phone, gender, birthDate, rating, isSuspended
        );
    }
}
