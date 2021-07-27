package com.bad.bigad.util;

import com.google.gson.Gson;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.bind.DatatypeConverter;
import java.util.Map;

@Slf4j
public enum Util {
    instance;

    public static Gson gson;
    static {
        gson = new Gson();
    }

    private SnowFlake snowFlake;
    {
        snowFlake = new SnowFlake(0, 1);
    }

    private SnowFlake mapSnow;
    {
        mapSnow = new SnowFlake(9, 1);
    }

    private SnowFlake roomSnow;
    {
        roomSnow = new SnowFlake(9,2);
    }

    private SnowFlake reelSnow;
    {
        reelSnow = new SnowFlake(9,3);
    }

    public long getSnowId() {
        return snowFlake.nextId();
    }

    public long getMapSnowId() {
        return mapSnow.nextId();
    }

    public long getRoomSnowId() {
        return roomSnow.nextId();
    }

    public long getReelSnowId() {
        return reelSnow.nextId();
    }

    public Map<String, Object> parseToken(String token, String key) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(key))
                    .parseClaimsJws(token).getBody();
        } catch (SignatureException | MalformedJwtException e) {
            log.info("token解析失败");
        } catch (ExpiredJwtException e) {
            log.info("token已过期");
        }
        return claims;
    }
}
