package com.pagely.userservice.domain.service;

import com.pagely.common.auth.Role;
import java.util.UUID;

public interface UserCredentialVerifier {

    record Result(UUID userId, Role role) {
    }

    Result verify(String loginId, String password);
}
