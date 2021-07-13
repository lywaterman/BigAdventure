insert into player(id, wx_name, wx_nick_name) values(12312312312, 'open111', '刘洋');
insert into player(id, wx_name, wx_nick_name) values(223323423423, 'open112', '刘大洋');

# 在db里面保存的就是变化了的数据
# 地图的id已经知道了地图的属性（大小）（脚本）
# 格子的id就知道格子对应的属性
insert into gamemap(id, status, grids) value (0, 1, '{"0|0": {"id":1, "status":1}}');