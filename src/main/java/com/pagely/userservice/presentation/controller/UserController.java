package com.pagely.userservice.presentation.controller;

import com.pagely.common.response.ApiResponse;
import com.pagely.userservice.application.service.UserApplicationService;
import com.pagely.userservice.presentation.dto.request.SignupRequest;
import com.pagely.userservice.presentation.dto.response.SignupResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
}
