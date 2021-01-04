set
    character_set_client = utf8mb4;
set
    character_set_connection = utf8mb4;

create table post(
    post_id bigint not null auto_increment,
    nickname varchar(63) not null,
    message varchar(1023) not null,
    post_datetime datetime not null,
    primary key(post_id)
) engine = InnoDB default charset = utf8mb4 collate = utf8mb4_bin comment '投稿情報';

