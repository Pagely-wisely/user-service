package com.pagely.userservice.presentation.controller;

import com.pagely.common.auth.Role;
import com.pagely.common.auth.annotation.AuthRequired;
import com.pagely.common.auth.annotation.CurrentUserId;
import com.pagely.common.auth.annotation.CurrentUserRole;
import com.pagely.common.response.ApiResponse;
import com.pagely.userservice.application.service.UserApplicationService;
import com.pagely.userservice.presentation.dto.request.SignupRequest;
import com.pagely.userservice.presentation.dto.response.SignupResponse;
import jakarta.validation.Valid;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User REST 컨트롤러.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;

    @PostMapping
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userApplicationService.signup(request.toCommand());
        return ApiResponse.created(response);
    }

    // TODO: 다음 PR에서 삭제 (정상 동작 확인 기록)
    @GetMapping("/test")
    @AuthRequired
    public ResponseEntity<ApiResponse> debugMe(
            @CurrentUserId UUID userId,
            @CurrentUserRole Role role
    ) {
        return ApiResponse.ok(Map.of(
                "userId", userId.toString(),
                "role", role.name()
        ));
    }

}
