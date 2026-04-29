package com.pagely.userservice.application.dto.query;

import com.pagely.common.auth.Role;
import com.pagely.userservice.domain.model.Gender;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;

/**
 * 검색 조건 DTO. Controller 쿼리 파라미터에서 자동 바인딩됨.
 * <p>
 * ex) GET /api/v1/users?keyword=홍&role=CREATOR&isSuspended=false
 */
@Getter
@Setter
public class UserSearchCondition {

    private List<UUID> ids; // MSA 내부 서비스에서 조회
    private String loginId;
    private String name;
    private String email;
    private String nickname;
    private Role role;
    private Gender gender;
    private Boolean isSuspended;

    // name, email, nickname 통합 검색
    private String keyword;

    // 감사 필드 조건 (관리자용)
    private LocalDateTime createdAtFrom;  // 가입일 시작
    private LocalDateTime createdAtTo;    // 가입일 종료
    private LocalDateTime updatedAtFrom;  // 수정일 시작
    private LocalDateTime updatedAtTo;    // 수정일 종료
    private UUID createdBy;               // 생성자 ID
    private UUID updatedBy;               // 수정자 ID

}
