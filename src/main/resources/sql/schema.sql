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
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;