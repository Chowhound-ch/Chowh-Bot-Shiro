create table if not exists bot_log
(
    log_id      INTEGER primary key autoincrement,
    method_id   TEXT not null,
    user_id     BIGINT not null,
    group_id    BIGINT,
    invoke_time datetime not null,
    command     TEXT
);
