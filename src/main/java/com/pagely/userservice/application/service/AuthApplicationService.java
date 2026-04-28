package com.pagely.userservice.application.service;

import com.pagely.common.exception.BusinessException;
import com.pagely.userservice.application.dto.LoginCommand;
import com.pagely.userservice.domain.exception.UserErrorCode;
import com.pagely.userservice.domain.model.User;
import com.pagely.userservice.domain.repository.UserRepository;
import com.pagely.userservice.domain.service.PasswordEncoder;
import com.pagely.userservice.infrastructure.security.JwtTokenProvider;
import com.pagely.userservice.presentation.dto.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 애플리케이션 서비스.
 *
 * <p>로그인 / 토큰 발급 / (향후) 토큰 재발급 / 로그아웃</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 로그인.
     *
     * <p><b>흐름</b></p>
     * <ol>
     *   <li>loginId 로 유저 조회</li>
     *   <li>비밀번호 검증</li>
     *   <li>정지 계정 검사</li>
     *   <li>JWT AccessToken 발급</li>
     * </ol>
     */
    @Transactional(readOnly = true)
    public TokenResponse login(LoginCommand command) {
        // 1. loginId 로 유저 조회 (없으면 INVALID_CREDENTIALS)
        User user = userRepository.findByLoginId(command.loginId())
                .orElseThrow(() -> {
                    log.debug("로그인 실패 - 존재하지 않는 loginId: {}", command.loginId());
                    return new BusinessException(UserErrorCode.INVALID_CREDENTIALS);
                });

        // 2. 비밀번호 검증 (불일치도 동일 에러)
        if (!passwordEncoder.matches(command.password(), user.getPassword().getHashedValue())) {
            log.debug("로그인 실패 - 비밀번호 불일치: loginId={}", command.loginId());
            throw new BusinessException(UserErrorCode.INVALID_CREDENTIALS);
        }

        // 3. 정지 계정 차단
        if (user.getIsSuspended()) {
            log.warn("로그인 실패 - 정지 계정 시도: userId={}", user.getId());
            throw new BusinessException(UserErrorCode.USER_SUSPENDED);
        }

        // 4. JWT 발급
        String accessToken = jwtTokenProvider.createAccessToken(
                user.getId(),
                user.getRole().name()
        );

        log.info("로그인 성공: userId={}, role={}", user.getId(), user.getRole());

        return TokenResponse.of(accessToken, jwtTokenProvider.getAccessTokenExpirationSeconds());
    }
}
