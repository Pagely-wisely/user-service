CREATE TABLE p_user_nickname_histories
(
    id           UUID PRIMARY KEY,

    user_id      UUID        NOT NULL,
    old_nickname VARCHAR(30) NOT NULL,
    new_nickname VARCHAR(30) NOT NULL,

    changed_at   TIMESTAMP   NOT NULL DEFAULT NOW(),
    changed_by   UUID        NOT NULL,

    reason       VARCHAR(50),

    CONSTRAINT fk_user_nickname_history_user
        FOREIGN KEY (user_id) REFERENCES p_users (id)
);
