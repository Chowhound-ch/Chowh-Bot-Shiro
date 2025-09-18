create table if not exists `group`
(
    id           bigint auto_increment
    primary key,
    group_id     bigint        null,
    group_status int default 0 null,
    create_time  datetime      null,
    modify_time  datetime      null,
    is_delete   int default 0 null,
    constraint group_id_index
    unique (group_id)
    );

create table if not exists user
(
    id          bigint auto_increment
    primary key,
    user_id     bigint        not null,
    role        int           null,
    create_time datetime      null,
    modify_time datetime      null,
    is_delete   int default 0 null,
    constraint user_id_index
    unique (user_id)
    );

