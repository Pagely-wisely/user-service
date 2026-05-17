package com.pagely.userservice.presentation.controller;

import com.pagely.common.response.ApiResponse;
import com.pagely.userservice.application.dto.query.UserSearchCondition;
import com.pagely.userservice.application.service.UserCredentialApplicationService;
import com.pagely.userservice.application.service.UserQueryService;
import com.pagely.userservice.presentation.dto.request.VerifyCredentialsRequest;
import com.pagely.userservice.presentation.dto.response.UserInfoResponse;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * MSA 내부 서비스 전용 컨트롤러. API Gateway에서 외부 접근 차단 (404)
 * TODO: JavaDoc -> 문서화
 */
@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserQueryService userQueryService;

    private final UserCredentialApplicationService credentialService;

    /**
     * <b>
     * 자격 검증 (Auth Service 의 로그인 흐름에서 호출)
     * </b>
     *
     */
    @PostMapping("/credential-verifications")
    public ResponseEntity<ApiResponse> verifyCredentials(
            @Valid @RequestBody VerifyCredentialsRequest request
    ) {
        return ApiResponse.ok(
                credentialService.verifyCredentials(
                        request.toCommand()
                )
        );
    }

    /**
     * <b>
     * 자격 조회 (Auth Service 의 토큰 재발급 등 에서 호출)
     * </b>
     *
     */
    @GetMapping("{userId}/auth-info")
    public ResponseEntity<ApiResponse> getUserIdentity(
            @PathVariable UUID userId
    ) {
        return ApiResponse.ok(
                credentialService.getUserIdentity(userId)
        );
    }

    /**
     * <b>내부 서비스용 단건 조회</b>
     * <ul>
     * <li><b>용도:</b> 특정 유저의 기본 프로필 정보가 필요할 때 사용 (모임원 정보 등)</li>
     * <li><b>호출 예시:</b> GET /internal/users/314fb54a-6bb3-46af-b7aa-799bd81bc047</li>
     * <li><b>Feign:</b> UserInfoResponse getUser(@PathVariable("userId") UUID userId)</li>
     * </ul>
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse> getUser(@PathVariable UUID userId) {
        return ApiResponse.ok(userQueryService.getUser(userId));
    }

    /**
     * <b>내부 서비스용 배치 조회</b>
     * <ul>
     * <li><b>용도:</b> 목록 페이지 등에서 여러 유저의 정보를 한꺼번에 조립할 때 사용</li>
     * <li><b>호출 예시:</b> GET /internal/users/batch?ids=uuid1,uuid2,uuid3</li>
     * <li><b>Feign:</b> List&lt;UserInfoResponse&gt; getUsersByIds(@RequestParam("ids") List&lt;UUID&gt; ids)</li>
     * <li><b>주의:</b> 많은 양의 ID 조회 시 URL 길이 제한(414)에 주의</li>
     * </ul>
     */
    @GetMapping("/batch")
    public ResponseEntity<ApiResponse> getUsersByIds(@RequestParam List<UUID> ids) {
        return ApiResponse.ok(userQueryService.getUsersByIds(ids));
    }

    /**
     * <b>내부 서비스용 조건 검색</b>
     * <ul>
     * <li><b>용도:</b> 특정 조건에 맞는 유저 목록 추출</li>
     * <li><b>Feign:</b> List&lt;UserInfoResponse&gt; searchUsers(@SpringQueryMap UserSearchCondition condition)</li>
     * <li><b>보안 정책:</b>
     * <ul>
     * <li>성능 보호를 위해 1회 호출 시 <b>최대 1,000건</b>으로 제한됨</li>
     * <li>전체 데이터가 필요할 경우 반복 호출(Paging) 필요</li>
     * <li>공통 모듈의 PageRequestArgumentResolver는 파라미터 타입이 PageRequest일 때만 동작 (v2.0.0 기준)</li>
     * </ul>
     * </li>
     * </ul>
     */
    @GetMapping
    public ResponseEntity<ApiResponse> searchUsers(
            UserSearchCondition condition,
            @PageableDefault(size = 1000) Pageable pageable
    ) {
        return ApiResponse.ok(
                userQueryService.searchUsers(condition, pageable),
                UserInfoResponse::from
        );
    }
}
