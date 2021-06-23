package com.bad.bigad.service.impl;

import com.bad.bigad.entity.Player;
import com.bad.bigad.mapper.PlayerMapper;
import com.bad.bigad.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Service
public class PlayerServiceImp implements PlayerService {
    @Autowired
    private PlayerMapper playerMapper;

    @Override
    public Player findById(int id) {
        return playerMapper.findById(id);
    }
}
