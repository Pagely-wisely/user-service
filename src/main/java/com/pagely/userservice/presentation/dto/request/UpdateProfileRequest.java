package com.pagely.userservice.presentation.dto.request;

import com.pagely.userservice.application.dto.command.UpdateProfileCommand;
import com.pagely.userservice.domain.model.Gender;
import jakarta.validation.constraints.Email;
import java.time.LocalDate;

public record UpdateProfileRequest(
        @Email String email,
        String name,
        String phone,
        Gender gender,
        LocalDate birthDate,
        String nickname
) {
    public UpdateProfileCommand toCommand() {
        return new UpdateProfileCommand(email, name, phone, gender, birthDate, nickname);
    }
}
