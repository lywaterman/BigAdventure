package com.bad.bigad.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigInteger;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Data
public class Player implements Serializable {
    private String idStr;
    @EqualsAndHashCode.Include
    private long id;
    private String wx_name;
    private String wx_nick_name;

    public String getIdStr() {
        if (idStr == null) {
            idStr = String.valueOf(id);
        }
        return idStr;
    }
}
