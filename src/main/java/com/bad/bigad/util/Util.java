package com.bad.bigad.util;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import javax.xml.bind.DatatypeConverter;
import java.util.Map;

@Slf4j
public enum Util {
    instance;

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
