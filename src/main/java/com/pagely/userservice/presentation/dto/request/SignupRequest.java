package com.pagely.userservice.presentation.dto.request;

import com.pagely.userservice.application.dto.command.SignupCommand;
import com.pagely.userservice.domain.model.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 회원가입 요청 DTO.
 *
 * <p>POST /api/v1/users - Request Body.</p>
 */
public record SignupRequest(
        @NotBlank(message = "로그인 아이디는 필수입니다.")
        @Size(min = 4, max = 50, message = "로그인 아이디는 4자 이상 50자 이하여야 합니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9_]+$",
                message = "로그인 아이디는 영문/숫자/언더스코어만 사용 가능합니다."
        )
        String loginId,

        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "유효한 이메일 형식이 아닙니다.")
        @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
        String email,

        // 비밀번호 정책 검증은 VO에서 담당
        @NotBlank(message = "비밀번호는 필수입니다.")
        String password,

        @NotBlank(message = "이름은 필수입니다.")
        @Size(max = 100, message = "이름은 100자 이하여야 합니다.")
        String name,

        @NotBlank(message = "닉네임은 필수입니다.")
        @Size(min = 2, max = 30, message = "닉네임은 2자 이상 30자 이하여야 합니다.")
        String nickname,

        @NotBlank(message = "전화번호는 필수입니다.")
        @Pattern(
                regexp = "^010-\\d{4}-\\d{4}$",
                message = "전화번호 형식이 올바르지 않습니다. (예: 010-1234-5678)"
        )
        String phone,

        @NotNull(message = "성별은 필수입니다.")
        Gender gender,

        @NotNull(message = "생년월일은 필수입니다.")
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate birthDate
) {
    public SignupCommand toCommand() {
        return new SignupCommand(
                loginId,
                email,
                password,
                name,
                nickname,
                phone,
                gender,
                birthDate
        );
    }
}
