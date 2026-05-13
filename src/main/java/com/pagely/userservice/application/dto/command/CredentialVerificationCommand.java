package com.pagely.userservice.application.dto.command;

public record CredentialVerificationCommand(
        String loginId,
        String password
) {
}
