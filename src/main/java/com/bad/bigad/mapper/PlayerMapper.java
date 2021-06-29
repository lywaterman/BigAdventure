package com.bad.bigad.mapper;

import com.bad.bigad.entity.Player;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;

//相当于DAO
@Mapper
public interface PlayerMapper {
    public Player findByWxName(String wx_name);
    public Player findById(long id);
    public void insertPlayer(Player player);
}
