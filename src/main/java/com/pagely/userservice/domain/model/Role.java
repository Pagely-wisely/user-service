package com.pagely.userservice.domain.model;

/**
 * 유저 권한 역할.
 *
 * <p>정책</p>
 * <ul>
 *   <li>MASTER: 관리자 (유저 정지/해제 등 관리 기능)</li>
 *   <li>USER: 일반 유저 (회원가입 기본값)</li>
 *   <li>CREATOR: 콘텐츠 제공자 (MVP 외 범위, 선언만 해둠)</li>
 * </ul>
 */
public enum Role {
    MASTER,
    USER,
    CREATOR
}
