package com.pagely.userservice.presentation.dto.request;

import com.pagely.userservice.application.dto.command.CredentialVerificationCommand;
import jakarta.validation.constraints.NotBlank;

public record VerifyCredentialsRequest(
        @NotBlank(message = "loginId 는 필수입니다.") String loginId,
        @NotBlank(message = "password 는 필수입니다.") String password
) {

    public CredentialVerificationCommand toCommand() {
        return new CredentialVerificationCommand(loginId, password);
    }
}
