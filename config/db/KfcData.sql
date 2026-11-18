create table if not exists kfc_data
(
    data_id     INTEGER primary key autoincrement,
    kfc_text    TEXT not null,
    create_time datetime,
    modify_time datetime,
    is_delete   INTEGER default 0 not null
);
