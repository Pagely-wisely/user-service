-- 1. ENUM 타입 생성
-- PostgreSQL에서는 테이블 생성 전 ENUM 타입을 먼저 정의해야 합니다.
CREATE TYPE user_role AS ENUM ('MASTER', 'USER', 'CREATOR');
CREATE TYPE user_gender AS ENUM ('MALE', 'FEMALE');

-- 2. p_users 테이블 생성
CREATE TABLE p_users (
                         id UUID PRIMARY KEY,
                         login_id VARCHAR(50) NOT NULL UNIQUE,
                         password VARCHAR(255) NOT NULL,
                         name VARCHAR(100) NOT NULL,
                         role user_role NOT NULL,
                         nickname VARCHAR(30) NOT NULL UNIQUE,
                         phone VARCHAR(20) NOT NULL,
                         gender user_gender NOT NULL,
                         birth_date DATE NOT NULL,
                         rating INTEGER NOT NULL,
                         is_suspended BOOLEAN NOT NULL DEFAULT false,
                         created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                         created_by UUID NOT NULL,
                         updated_at TIMESTAMP,
                         updated_by UUID,
                         deleted_at TIMESTAMP,
                         deleted_by UUID
);

-- 3. 검색 성능 최적화를 위한 인덱스 생성
-- 로그인이 빈번하게 일어남을 대비해 login_id에 인덱스를 걸기
CREATE INDEX idx_users_login_id ON p_users (login_id);
