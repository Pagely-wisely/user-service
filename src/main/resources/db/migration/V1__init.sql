-- ============================================================
-- V1: p_users 테이블 생성
-- ============================================================
--   - ENUM은 VARCHAR + CHECK 제약으로 관리 (값 추가/변경 유연)
--   - JPA @Enumerated(EnumType.STRING)과 자연스럽게 매칭
-- ============================================================

CREATE TABLE p_users
(
    id           UUID PRIMARY KEY,
    login_id     VARCHAR(50)  NOT NULL,
    password     VARCHAR(255) NOT NULL,
    name         VARCHAR(100) NOT NULL,
    role         VARCHAR(20)  NOT NULL,
    nickname     VARCHAR(30)  NOT NULL,
    email        VARCHAR(100) NOT NULL,
    phone        VARCHAR(20)  NOT NULL,
    gender       VARCHAR(10)  NOT NULL,
    birth_date   DATE         NOT NULL,
    rating       INTEGER      NOT NULL DEFAULT 1000,
    is_suspended BOOLEAN      NOT NULL DEFAULT false,
    created_at   TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by   UUID         NOT NULL,
    updated_at   TIMESTAMP,
    updated_by   UUID,
    deleted_at   TIMESTAMP,
    deleted_by   UUID,

    CONSTRAINT ck_p_users_role CHECK (role IN ('USER', 'MASTER', 'CREATOR')),
    CONSTRAINT ck_p_users_gender CHECK (gender IN ('MALE', 'FEMALE'))
);

-- ============================================================
-- Partial Unique Indexes
-- Soft delete 된 레코드는 제외하여, 동일 login_id/email/nickname 으로
-- 재가입 가능하도록 함.
-- ============================================================

CREATE UNIQUE INDEX uk_p_users_login_id
    ON p_users (login_id) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX uk_p_users_email
    ON p_users (email) WHERE deleted_at IS NULL;

CREATE UNIQUE INDEX uk_p_users_nickname
    ON p_users (nickname) WHERE deleted_at IS NULL;
