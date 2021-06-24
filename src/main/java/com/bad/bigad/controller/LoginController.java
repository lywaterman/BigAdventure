package com.bad.bigad.controller;

import com.bad.bigad.entity.Player;
import com.bad.bigad.service.PlayerService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.Data;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.math.BigInteger;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;

@Data
class LoginParam {
    private String wx_name;
    private String wx_nick_name;
}

@Data
class LoginResult {
    private String user_nick_name;
    private String token;
}

@RestController
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Value("${jwt_key}")
    String jwtKey;

    @Value("${jwt_minute}")
    int jwtMinute;

    @Value("${jwt_pub}")
    String jwtPub;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    PlayerService playerService;

    static <T> Optional<T> or(Optional<T> first, Optional<T> second) {
        return first.isPresent() ? first : second;
    }

    @RequestMapping("/login")
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public LoginResult login(@RequestBody LoginParam param) {
        //验证参数

        Lock lock = redissonClient.getLock(param.getWx_name());

        Boolean canLock = lock.tryLock();
        if (!canLock) {
            return null;
        }

        Player player = null;
        //验证通过
        try {
            RMap<String, Player> m = redissonClient.getMap("players");
            player = m.get(param.getWx_name());

            if (player == null) {
                player = playerService.findByWxName(param.getWx_name());
                if (player != null) {
                    m.put(player.getWx_name(), player);
                }

                if (player == null) {
                    player = playerService.CreateNew(param.getWx_name(), param.getWx_nick_name());
                    if (player != null) {
                        m.put(player.getWx_name(), player);
                    }
                }
            }
        } finally {
            lock.unlock();
        }

        LoginResult loginResult = new LoginResult();
        loginResult.setUser_nick_name(player.getWx_nick_name());
        loginResult.setToken(createPlayerToken(player));
        return loginResult;
    }

    @RequestMapping("/test")
    public Object test() {
        Map<String,Object> map = new HashMap<>();
        map.put("ly","测试");
        String token = creatToken(map);
        return token;
    }
    @RequestMapping("/test1")
    public Object test1(@RequestParam String token) {
        parseToken(token);
        return token;
    }

    public String createPlayerToken(Player player) {
        Map<String,Object> map = new HashMap<>();
        map.put("wx_name", player.getWx_name());
        map.put("wx_nick_name", player.getWx_nick_name());
        return creatToken(map);
    }

    public  String creatToken(Map<String,Object> params) {
        SignatureAlgorithm signature = SignatureAlgorithm.HS256;

        byte[] secretBytes = DatatypeConverter.parseBase64Binary(jwtKey);
        Key secretKey = new SecretKeySpec(secretBytes, signature.getJcaName());
        Long expiration = System.currentTimeMillis() + jwtMinute * 60 * 1000L;
        Claims claims = new DefaultClaims(params);
        claims.setIssuer(jwtPub);
        claims.setIssuedAt(new Date());
        claims.setExpiration(new Date(expiration));
        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .signWith(signature, secretKey);

        return builder.compact();
    }

    public Map<String, Object> parseToken(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser().setSigningKey(DatatypeConverter.parseBase64Binary(jwtKey))
                    .parseClaimsJws(token).getBody();
        } catch (SignatureException | MalformedJwtException e) {
            logger.info("token解析失败");
        } catch (ExpiredJwtException e) {
            logger.info("token已过期");
        }
        return claims;
    }
}
