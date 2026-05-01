-- Outbox 테이블
-- DB 트랜잭션 안에서 이벤트 기록 → 별도 Poller 가 Kafka 로 발행

CREATE TABLE p_outbox
(
    -- 기본 식별자
    id              UUID PRIMARY KEY,

    -- 이벤트 메타
    aggregate_type  VARCHAR(50)  NOT NULL,                 -- USER, MEETING 등
    aggregate_id    UUID         NOT NULL,                 -- 엔티티 ID (메시지 키로 활용)
    event_type      VARCHAR(100) NOT NULL,                 -- USER_CREATED, USER_UPDATED 등
    topic           VARCHAR(100) NOT NULL,                 -- user.created, user.updated 등

    -- 페이로드
    payload         JSONB        NOT NULL,                 -- 이벤트 본문 (JSON)

    -- 발행 추적
    published       BOOLEAN      NOT NULL DEFAULT FALSE,
    published_at    TIMESTAMP,
    failure_count   INT          NOT NULL DEFAULT 0,
    last_failure_at TIMESTAMP,
    last_failure_message TEXT,

    -- 감사 필드 (BaseEntity 와 동일)
    created_at      TIMESTAMP    NOT NULL,
    created_by      UUID         NOT NULL,
    updated_at      TIMESTAMP,
    updated_by      UUID,
    deleted_at      TIMESTAMP,
    deleted_by      UUID
);

-- Poller 가 가장 자주 사용할 인덱스
-- "미발행 이벤트를 created_at 순으로 조회"
CREATE INDEX idx_outbox_unpublished
    ON p_outbox (created_at)
    WHERE published = FALSE AND deleted_at IS NULL;

-- 디버깅 / 운영용 — 특정 aggregate 의 이벤트 조회
CREATE INDEX idx_outbox_aggregate
    ON p_outbox (aggregate_type, aggregate_id)
    WHERE deleted_at IS NULL;

-- 코멘트 (PostgreSQL)
COMMENT ON TABLE p_outbox IS 'Outbox 패턴 이벤트 저장소. Poller 가 published=false 인 이벤트를 Kafka 로 발행';
COMMENT ON COLUMN p_outbox.aggregate_type IS '이벤트가 속한 도메인 타입 (USER, MEETING 등)';
COMMENT ON COLUMN p_outbox.aggregate_id IS '이벤트의 entity ID. Kafka 메시지 키로 사용 (순서 보장)';
COMMENT ON COLUMN p_outbox.event_type IS '구체적 이벤트 타입 (USER_CREATED 등)';
COMMENT ON COLUMN p_outbox.topic IS '발행 대상 Kafka 토픽 (user.created 등)';
COMMENT ON COLUMN p_outbox.payload IS '이벤트 본문 JSON';
COMMENT ON COLUMN p_outbox.published IS '발행 완료 여부';
COMMENT ON COLUMN p_outbox.failure_count IS '발행 실패 횟수 (재시도 추적)';
