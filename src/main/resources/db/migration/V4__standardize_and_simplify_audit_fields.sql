-- ============================================================
-- 1. p_user_nickname_histories 컬럼명 수정
-- ============================================================
ALTER TABLE p_user_nickname_histories
    RENAME COLUMN changed_at TO created_at;

ALTER TABLE p_user_nickname_histories
    RENAME COLUMN changed_by TO created_by;

COMMENT ON COLUMN p_user_nickname_histories.created_at IS '닉네임 생성/변경 이벤트 발생 시각';
COMMENT ON COLUMN p_user_nickname_histories.created_by IS '닉네임 생성/변경 이벤트 행위자';

-- ============================================================
-- 2. p_outbox 에서 의미 약한 audit 컬럼 정리
-- ============================================================
ALTER TABLE p_outbox DROP COLUMN created_by;
ALTER TABLE p_outbox DROP COLUMN updated_at;
ALTER TABLE p_outbox DROP COLUMN updated_by;
ALTER TABLE p_outbox DROP COLUMN deleted_at;
ALTER TABLE p_outbox DROP COLUMN deleted_by;

-- 인덱스 재설정
DROP INDEX IF EXISTS idx_outbox_unpublished;
CREATE INDEX idx_outbox_unpublished
    ON p_outbox (created_at)
    WHERE published = FALSE;

DROP INDEX IF EXISTS idx_outbox_aggregate;
CREATE INDEX idx_outbox_aggregate
    ON p_outbox (aggregate_type, aggregate_id);

-- 코멘트
COMMENT ON COLUMN p_outbox.created_at IS 'Outbox row 생성 시각';
