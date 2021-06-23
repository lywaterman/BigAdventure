package com.bad.bigad.entity;

import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;

@Data
public class Player implements Serializable {
    private int id;
    private String wx_name;
    private String wx_nick_name;
}
