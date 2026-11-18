create table if not exists data_dict_cat
(
    cat_code    TEXT primary key not null,
    cat_name    TEXT not null,
    create_time datetime,
    modify_time datetime,
    is_delete   integer default 0 not null
);

create table if not exists data_dict_cat_detail
(
    detail_id   INTEGER primary key autoincrement,
    cat_code    TEXT not null references data_dict_cat (cat_code),
    dict_code   TEXT not null,
    dict_name   TEXT not null,
    create_time datetime,
    modify_time datetime,
    is_delete   integer default 0 not null,
    unique (cat_code, dict_code)
);

insert into data_dict_cat (cat_code, cat_name, create_time, modify_time)
values ('EVENT_FLAG', '订阅事件', current_timestamp, current_timestamp)
on conflict (cat_code) do nothing;

insert into data_dict_cat_detail (cat_code, dict_code, dict_name, create_time, modify_time)
values ('EVENT_FLAG', 'moyu', '摸鱼人日历', current_timestamp, current_timestamp),
       ('EVENT_FLAG', '今日金价', '今日金价', current_timestamp, current_timestamp)
on conflict (cat_code, dict_code) do nothing;
