package com.pagely.userservice.application.service;

import com.pagely.common.exception.BusinessException;
import com.pagely.userservice.application.dto.command.SignupCommand;
import com.pagely.userservice.application.dto.command.UpdateInfoCommand;
import com.pagely.userservice.application.dto.command.UpdateProfileCommand;
import com.pagely.userservice.domain.exception.UserErrorCode;
import com.pagely.userservice.domain.model.NicknameChangeReason;
import com.pagely.userservice.domain.model.User;
import com.pagely.userservice.domain.model.UserNicknameHistory;
import com.pagely.userservice.domain.model.vo.Password;
import com.pagely.userservice.domain.repository.UserNicknameHistoryRepository;
import com.pagely.userservice.domain.repository.UserRepository;
import com.pagely.userservice.domain.service.PasswordEncoder;
import com.pagely.userservice.presentation.dto.response.SignupResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService {
    private final UserNicknameHistoryRepository nicknameHistoryRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupCommand command) {
        // 1. 중복 검사
        validateDuplicate(command);

        // 2. 도메인 객체 생성
        Password password = Password.of(command.password(), passwordEncoder);
        User user = User.create(
                command.loginId(),
                command.email(),
                password,
                command.name(),
                command.nickname(),
                command.phone(),
                command.gender(),
                command.birthDate()
        );
        // 회원가입으로 닉네임 변경 이력 생성
        UserNicknameHistory nicknameHistory = UserNicknameHistory.of(
                user.getId(),
                null,
                user.getNickname(),
                user.getId(),
                NicknameChangeReason.CREATE
        );

        // 3. 저장 (DB UNIQUE 제약 위반 시 race condition 방어)
        try {
            User saved = userRepository.save(user);
            nicknameHistoryRepository.save(nicknameHistory);
            return SignupResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            // 사전 검사 통과했으나 동시 가입 race condition 발생
            log.warn("회원가입 동시성 충돌: loginId={}, email={}",
                    command.loginId(), command.email(), e);
            // @Transactional에 의해 User와 History 모두를 롤백
            throw resolveDuplicateException(command, e);
        }
    }

    /**
     * 사전 중복 검사. existsBy* 메서드 활용.
     */
    private void validateDuplicate(SignupCommand command) {
        if (userRepository.existsByLoginId(command.loginId())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_LOGIN_ID);
        }
        if (userRepository.existsByEmail(command.email())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByNickname(command.nickname())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }
    }

    /**
     * DataIntegrityViolationException의 메시지에서 충돌 컬럼 식별.
     */
    private BusinessException resolveDuplicateException(
            SignupCommand command,
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

    /**
     * 본인 프로필 수정: 닉네임 변경 30일 쿨타임 정책 적용
     *
     * @param userId:  변경하는 본인
     * @param command: 수정 정보(들)
     */
    @Transactional
    public void updateMyProfile(UUID userId, UpdateProfileCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        String oldNickname = user.getNickname();
        if (command.nickname() != null) {
            LocalDateTime lastChangedAt = nicknameHistoryRepository
                    .findFirstByUserIdOrderByChangedAtDesc(userId)
                    .map(UserNicknameHistory::getChangedAt)
                    .orElse(null);
            validateNickname30Days(lastChangedAt);
            user.changeNickname(command.nickname());

            nicknameHistoryRepository.save(UserNicknameHistory.of(
                    userId, oldNickname, command.nickname(),
                    userId, NicknameChangeReason.USER_CHANGE
            ));
        }

        user.updateMyProfile(
                command.email(),
                command.name(),
                command.phone(),
                command.gender(),
                command.birthDate()
        );
    }

    /**
     * 마스터 임의 수정
     *
     * @param currentUserId 수정자
     * @param userId        수정 타겟
     * @param command       수정 정보(들)
     */
    @Transactional
    public void adminUpdateInfo(UUID currentUserId, UUID userId, UpdateInfoCommand command) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(UserErrorCode.USER_NOT_FOUND));

        String oldNickname = user.getNickname();
        if (command.nickname() != null) {
            nicknameHistoryRepository.save(
                    UserNicknameHistory.of(
                            userId,
                            oldNickname,
                            command.nickname(),
                            currentUserId,
                            NicknameChangeReason.ADMIN_CHANGE
                    )
            );
            user.changeNickname(command.nickname());
        }

        user.adminUpdateInfo(
                command.email(),
                command.name(),
                command.role(),
                command.phone(),
                command.gender(),
                command.birthDate(),
                command.rating(),
                command.isSuspended()
        );
    }

    /**
     * 닉네임 변경 30일 제한 검증
     *
     */
    private void validateNickname30Days(LocalDateTime lastChangedAt) {
        if (lastChangedAt != null && lastChangedAt.plusDays(30).isAfter(LocalDateTime.now())) {
            throw new BusinessException(UserErrorCode.NICKNAME_CHANGE_LIMIT);
        }
    }
}
