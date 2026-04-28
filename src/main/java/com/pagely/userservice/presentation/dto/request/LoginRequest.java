package com.pagely.userservice.presentation.dto.request;

import com.pagely.userservice.application.dto.LoginCommand;
import jakarta.validation.constraints.NotBlank;

/**
 * 로그인 요청 DTO.
 *
 * <p>POST /api/v1/auth/token 의 Request Body.</p>
 */
public record LoginRequest(
        @NotBlank(message = "로그인 아이디는 필수입니다.")
        String loginId,

        @NotBlank(message = "비밀번호는 필수입니다.")
        String password
) {
    public LoginCommand toCommand() {
        return new LoginCommand(
                loginId,
                password
        );
    }
}
