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
