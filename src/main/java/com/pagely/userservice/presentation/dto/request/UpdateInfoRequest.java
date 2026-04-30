package com.pagely.userservice.presentation.dto.request;

import com.pagely.common.auth.Role;
import com.pagely.userservice.application.dto.command.UpdateInfoCommand;
import com.pagely.userservice.domain.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

public record UpdateInfoRequest(
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
        String email,
        @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
        String name,
        Role role,
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다.")
        String nickname,
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)"
        )
        String phone,
        Gender gender,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
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
