package com.pagely.userservice.application.service;

import com.pagely.common.exception.BusinessException;
import com.pagely.userservice.domain.exception.UserErrorCode;
import com.pagely.userservice.domain.model.User;
import com.pagely.userservice.domain.model.vo.Password;
import com.pagely.userservice.domain.repository.UserRepository;
import com.pagely.userservice.domain.service.PasswordEncoder;
import com.pagely.userservice.presentation.dto.request.SignupRequest;
import com.pagely.userservice.presentation.dto.response.SignupResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 1. 중복 검사
        validateDuplicate(request);

        // 2. 도메인 객체 생성
        Password password = Password.of(request.password(), passwordEncoder);
        User user = User.create(
                request.loginId(),
                request.email(),
                password,
                request.name(),
                request.nickname(),
                request.phone(),
                request.gender(),
                request.birthDate()
        );

        // 3. 저장 (DB UNIQUE 제약 위반 시 race condition 방어)
        try {
            User saved = userRepository.save(user);
            log.info("회원가입 완료: userId={}, email={}", saved.getId(), saved.getEmail());
            return SignupResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            // 사전 검사 통과했으나 동시 가입 race condition 발생
            log.warn("회원가입 동시성 충돌: loginId={}, email={}",
                    request.loginId(), request.email(), e);
            throw resolveDuplicateException(request, e);
        }
    }

    /**
     * 사전 중복 검사. existsBy* 메서드 활용.
     */
    private void validateDuplicate(SignupRequest request) {
        if (userRepository.existsByLoginId(request.loginId())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(request.nickname())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    /**
     * DataIntegrityViolationException의 메시지에서 충돌 컬럼 식별.
     */
    private BusinessException resolveDuplicateException(
            SignupRequest request,
            DataIntegrityViolationException e
    ) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

        if (message.contains("login_id")) {
            return new BusinessException(UserErrorCode.DUPLICATE_LOGIN_ID, e);
        }
        if (message.contains("email")) {
            return new BusinessException(UserErrorCode.DUPLICATE_EMAIL, e);
        }
        if (message.contains("nickname")) {
            return new BusinessException(UserErrorCode.DUPLICATE_NICKNAME, e);
        }

        // 어느 컬럼인지 모르면 일반 충돌로 처리
        return new BusinessException(UserErrorCode.DUPLICATE_LOGIN_ID, e);
    }
}
