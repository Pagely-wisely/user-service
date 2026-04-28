package com.pagely.userservice.application.dto;

public record LoginCommand(
        String loginId,
        String password
) {
}
