set
    character_set_client = utf8mb4;
set
    character_set_connection = utf8mb4;

create table post(
    nickname varchar(63) not null,
    message varchar(1023) not null,
    post_datetime datetime not null
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_bin comment '投稿情報';

