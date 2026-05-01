package com.pagely.userservice.presentation.controller;

import com.pagely.common.auth.Role;
import com.pagely.common.auth.annotation.AuthRequired;
import com.pagely.common.auth.annotation.CurrentUserId;
import com.pagely.common.pagination.PageRequest;
import com.pagely.common.response.ApiResponse;
import com.pagely.userservice.application.dto.query.UserSearchCondition;
import com.pagely.userservice.application.service.UserApplicationService;
import com.pagely.userservice.application.service.UserQueryService;
import com.pagely.userservice.presentation.dto.request.SignupRequest;
import com.pagely.userservice.presentation.dto.request.UpdateInfoRequest;
import com.pagely.userservice.presentation.dto.request.UpdateProfileRequest;
import com.pagely.userservice.presentation.dto.response.AdminUserInfoResponse;
import com.pagely.userservice.presentation.dto.response.SignupResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User REST 컨트롤러. (외부 API)
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserApplicationService userApplicationService;
    private final UserQueryService userQueryService;

    // [생성] 회원가입
    @PostMapping
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userApplicationService.signup(request.toCommand());
        return ApiResponse.created(response);
    }

    // [목록 조회] 전체 사용자 목록 (관리자용)
    @GetMapping
    @AuthRequired(role = Role.MASTER)
    public ResponseEntity<ApiResponse> searchUsers(
            UserSearchCondition condition,
            PageRequest pageRequest
    ) {
        return ApiResponse.ok(
                userQueryService.searchUsers(condition, pageRequest.toPageable()),
                AdminUserInfoResponse::from
        );
    }

    // [단건 조회] 특정 사용자 조회 (관리자용)
    @GetMapping("/{userId}")
    @AuthRequired(role = Role.MASTER)
    public ResponseEntity<ApiResponse> getAdminUser(@PathVariable UUID userId) {
        return ApiResponse.ok(userQueryService.getUserDetail(userId));
    }

    // [단건 수정] 특정 사용자 정보 수정 (관리자용)
    @PatchMapping("/{userId}")
    @AuthRequired(role = Role.MASTER)
    public ResponseEntity<Void> adminUpdateInfo(
            @CurrentUserId UUID currentUserId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateInfoRequest request) {
        userApplicationService.adminUpdateInfo(currentUserId, userId, request.toCommand());
        return ResponseEntity.ok().build();
    }

    // [단건 조회] 내 정보 조회
    @GetMapping("/me")
    @AuthRequired
    public ResponseEntity<ApiResponse> getUser(
            @CurrentUserId UUID currentUserId) {
        return ApiResponse.ok(userQueryService.getUser(currentUserId));
    }

    // [단건 수정] 내 정보 수정
    @PatchMapping("/me")
    @AuthRequired
    public ResponseEntity<Void> updateMyProfile(
            @CurrentUserId UUID currentUserId,
            @Valid @RequestBody UpdateProfileRequest request) {
        userApplicationService.updateMyProfile(
                currentUserId, request.toCommand());
        return ResponseEntity.ok().build();
    }
}
