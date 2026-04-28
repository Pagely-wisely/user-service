package com.pagely.userservice.presentation.controller;

import com.pagely.common.response.ApiResponse;
import com.pagely.userservice.application.service.AuthApplicationService;
import com.pagely.userservice.presentation.dto.request.LoginRequest;
import com.pagely.userservice.presentation.dto.response.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 REST 컨트롤러.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthApplicationService authApplicationService;

    @PostMapping("/token")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse response = authApplicationService.login(request.toCommand());
        return ApiResponse.ok(response);
    }
}
