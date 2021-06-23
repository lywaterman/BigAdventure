package com.bad.bigad.mapper;

import com.bad.bigad.entity.Player;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigInteger;

@Mapper
public interface PlayerMapper {
    public Player findByWxName(String wx_name);
    public Player findById(int id);
}
