package com.pagely.userservice.infrastructure.persistence;

import com.pagely.userservice.domain.model.User;
import com.pagely.userservice.domain.repository.UserRepository;
import com.pagely.userservice.infrastructure.persistence.jpa.JpaUserRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final JpaUserRepository jpaUserRepository;

    @Override
    public User save(User user) {
        return jpaUserRepository.saveAndFlush(user); // DataIntegrityViolationException catch
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaUserRepository.findById(id);
    }

    @Override
    public Optional<User> findByLoginId(String loginId) {
        return jpaUserRepository.findByLoginId(loginId);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaUserRepository.findByEmail(email);
    }

    @Override
    public boolean existsByLoginId(String loginId) {
        return jpaUserRepository.existsByLoginId(loginId);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByNickname(String nickname) {
        return jpaUserRepository.existsByNickname(nickname);
    }
}
