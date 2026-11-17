create table if not exists `group`
(
    id           bigint auto_increment
    primary key,
    group_id     bigint        null,
    group_status int default 0 null,
    create_time  datetime      null,
    modify_time  datetime      null,
    is_delete   int default 0 not null,
    constraint group_id_index
    unique (group_id)
    );

create table if not exists `user`
(
    id          bigint auto_increment
    primary key,
    user_id     bigint        not null,
    password    TEXT          null,
    role        int           null,
    create_time datetime      null,
    modify_time datetime      null,
    is_delete   int default 0 not null,
    constraint user_id_index
    unique (user_id)
    );

-- 旧表允许 NULL，在启用逻辑删除过滤后仍将这类记录视为有效。
update `group` set is_delete = 0 where is_delete is null;
update `user` set is_delete = 0 where is_delete is null;

create table if not exists `event_permit`
(
    permit_id   INTEGER
        constraint event_permit_pk
            primary key autoincrement,
    event_flag  TEXT,
    permit_type integer default 0,
    target      bigint,
    create_time datetime,
    modify_time datetime,
    is_delete   integer default 0 not null
);

create index if not exists event_permit_active_flag_idx
    on event_permit (event_flag, permit_type, target) where is_delete = 0;
create index if not exists event_permit_active_target_idx
    on event_permit (target, permit_type) where is_delete = 0;