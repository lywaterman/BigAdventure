drop table if exists player;
create table player (
    id int auto_increment,
    wx_name varchar(255) unique,
    wx_nick_name varchar(255),
    primary key (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;