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

create table if not exists `user`
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

create table if not exists `data_dict`
(
    dict_id     INTEGER
    constraint data_dict_pk
    primary key autoincrement,
    dict_code   TEXT,
    dict_name   TEXT,
    create_time datetime,
    modify_time datetime,
    is_delete   integer default 0,
    dict_value TEXT
)
