drop table if exists player;
create table player (
    id bigint UNIQUE ,
    wx_name varchar(255) DEFAULT NULL UNIQUE ,
    wx_nick_name varchar(255) DEFAULT NULL,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists gamemap;
create table gamemap (
    id bigint UNIQUE,
    temp_id int,
    status int,
    grids json,
    create_time datetime default now(),
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists gameroom;
create table gameroom (
    id bigint UNIQUE,
    map_id bigint,
    room_type int,
    map_refresh_time datetime,
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

drop table if exists gamereel;
create table gamereel (
    id bigint UNIQUE,
    temp_id int,
    cur_frag int,
    owner_id bigint,
    primary key (id)
)