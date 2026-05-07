package com.pagely.userservice.application.service;

import com.pagely.common.auth.UserContext;
import com.pagely.common.auth.UserContextHolder;
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
import com.pagely.userservice.infrastructure.messaging.event.UserCreatedEvent;
import com.pagely.userservice.presentation.dto.response.SignupResponse;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService {
    private final UserNicknameHistoryRepository nicknameHistoryRepository;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponse signup(SignupCommand command) {
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
                NicknameChangeReason.CREATE
        );
        // AuditorAware가 신규 유저 본인 ID를 읽어갈 수 있도록 컨텍스트 주입
        UserContextHolder.set(new UserContext(user.getId(), user.getRole()));
        try {
            User saved = userRepository.save(user); // save() 대신 saveAndFlush()를 사용하여 즉시 제약 조건을 검사함
            nicknameHistoryRepository.save(nicknameHistory);
            eventPublisher.publishEvent(UserCreatedEvent.from(saved));
            return SignupResponse.from(saved);
        } catch (DataIntegrityViolationException e) {
            throw resolveDuplicateException(e);
        } finally {
            UserContextHolder.clear(); // ThreadLocal 정리
        }
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
        if (command.nickname() != null && !command.nickname().equals(oldNickname)) {
            LocalDateTime lastChangedAt = nicknameHistoryRepository
                    .findFirstByUserIdOrderByCreatedAtDesc(userId)
                    .map(UserNicknameHistory::getCreatedAt)
                    .orElse(null);
            validateNickname30Days(lastChangedAt);

            user.changeNickname(command.nickname());

            nicknameHistoryRepository.save(UserNicknameHistory.of(
                    userId, oldNickname, command.nickname(),
                    NicknameChangeReason.USER_CHANGE
            ));
        }

        user.updateMyProfile(
                command.email(),
                command.name(),
                command.phone(),
                command.gender(),
                command.birthDate()
        );
        try {
            userRepository.save(user);

        } catch (DataIntegrityViolationException e) {
            throw resolveDuplicateException(e);
        }
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
        if (command.nickname() != null && !command.nickname().equals(oldNickname)) {
            nicknameHistoryRepository.save(
                    UserNicknameHistory.of(
                            userId,
                            oldNickname,
                            command.nickname(),
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
        try {
            userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw resolveDuplicateException(e);
        }
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

    /**
     * DataIntegrityViolationException의 메시지에서 충돌 컬럼 식별.
     */
    private BusinessException resolveDuplicateException(DataIntegrityViolationException e) {
        String message = e.getMessage() == null ? "" : e.getMessage().toLowerCase();

        //log.error("=== DataIntegrityViolation message: {}", message); // 임시 로그 추가

        // constraint [...] 부분만 추출해서 판단
        if (message.contains("uk_p_users_login_id")) {
            return new BusinessException(UserErrorCode.DUPLICATE_LOGIN_ID, e);
        }
        if (message.contains("uk_p_users_email")) {
            return new BusinessException(UserErrorCode.DUPLICATE_EMAIL, e);
        }
        if (message.contains("uk_p_users_nickname")) {
            return new BusinessException(UserErrorCode.DUPLICATE_NICKNAME, e);
        }

        // 어느 컬럼인지 모르면 일반 충돌로 처리
        return new BusinessException(UserErrorCode.DUPLICATE, e);
    }
}
