package com.pagely.userservice.application.service;

import com.pagely.common.exception.BusinessException;
import com.pagely.userservice.application.dto.command.CredentialVerificationCommand;
import com.pagely.userservice.domain.exception.UserErrorCode;
import com.pagely.userservice.domain.model.User;
import com.pagely.userservice.domain.repository.UserRepository;
import com.pagely.userservice.domain.service.PasswordEncoder;
import com.pagely.userservice.presentation.dto.response.CredentialsVerificationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 자격 검증 서비스 (내부 API 전용).
 *
 * <p>Auth Service 의 로그인 흐름에서 호출.</p>
 *
 * <p><b>보안 결정</b></p>
 * <ul>
 *   <li>"사용자 없음" / "비밀번호 불일치" 같은 응답 (Enumeration 공격 방지)</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserCredentialApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public CredentialsVerificationResponse verifyCredentials(CredentialVerificationCommand command) {
        User user = userRepository.findByLoginId(command.loginId())
                .orElseThrow(() -> {
                    log.debug("자격 검증 실패 — 사용자 없음 — loginId={}", command.loginId());
                    return new BusinessException(UserErrorCode.INVALID_CREDENTIALS);
                });

        if (!user.getPassword().matches(command.password(), passwordEncoder)) {
            log.debug("자격 검증 실패 — 비밀번호 불일치 — userId={}", user.getId());
            throw new BusinessException(UserErrorCode.INVALID_CREDENTIALS);
        }

        return CredentialsVerificationResponse.of(user.getId(), user.getRole());
    }
}
