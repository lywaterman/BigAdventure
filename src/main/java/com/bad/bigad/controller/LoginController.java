package com.bad.bigad.controller;

import com.bad.bigad.config.RabbitmqConfig;
import com.bad.bigad.entity.Player;
import com.bad.bigad.service.PlayerService;
import com.bad.bigad.util.Util;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.DefaultClaims;
import lombok.Data;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.crypto.spec.SecretKeySpec;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

@Data
class LoginParam {
    @NotBlank(message="wx_name cannot be missing or empty")
    private String wx_name;
    private String wx_nick_name;
}

@Data
class LoginParamToken {
    @NotBlank(message="token cannot be missing or empty")
    private String token;
}

@Data
class LoginResult {
    private int result;
    private String desc;
    private String id;
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

    @Autowired
    RabbitTemplate rabbitTemplate;

    @RequestMapping("/loginWithToken")
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public LoginResult loginWithToken(@Valid @RequestBody LoginParamToken param) {
        Map<String, Object> claims = Util.instance.parseToken(param.getToken(), jwtKey);

        LoginResult loginResult = new LoginResult();

        if(claims == null) {
            //token?????????????????????????????????
            loginResult.setResult(1);
            loginResult.setDesc("token?????????");
            return loginResult;
        }

        String idStr = (String) claims.get("id");
        Long id = Long.parseLong(idStr);

        RMap<Long, Player> players = redissonClient.getMap("players");
        Player player = players.get(id);

        if (player == null) {
            //????????????????????????????????????
            //?????????id???????????????id????????????redis
            Lock lock = redissonClient.getLock("lock_"+idStr);

            Boolean canLock = lock.tryLock();
            if (!canLock) {
                return null;
            }
            try {
                //??????????????????????????????
                player = playerService.findById(id);
                if (player != null) {
                    players.put(player.getId(), player);
                }

                if (player == null) {
                    //??????????????????????????????????????????
                    loginResult.setResult(2);
                    loginResult.setDesc("??????????????????");
                }
            } finally {
                lock.unlock();
            }
        } else {

        }

        loginResult.setUser_nick_name(player.getWx_nick_name());

        return loginResult;
    }

    //?????????????????????, ????????????????????????redis
    @RequestMapping("/login")
    @PostMapping(
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
    )
    public LoginResult login(@Valid @RequestBody LoginParam param) {
        //????????????

        //??????????????????????????????

        Player player = null;
        //????????????
        RMap<String, Long> wxToId = redissonClient.getMap("wxToId");
        Long id = wxToId.get(param.getWx_name());

        RMap<Long, Player> players = redissonClient.getMap("players");
        if (id != null) {
            player = players.get(id);
        }

        if (id == null || player == null) {
            //????????????????????????????????????
            //?????????id???????????????id????????????redis
            Lock lock = redissonClient.getLock("lock_"+param.getWx_name());

            Boolean canLock = lock.tryLock();
            if (!canLock) {
                return null;
            }
            try {
                //??????????????????????????????
                player = playerService.findByWxName(param.getWx_name());
                if (player != null) {
                    players.put(player.getId(), player);
                    wxToId.put(player.getWx_name(), player.getId());
                }

                if (player == null) {
                    player = playerService.newPlayer(param.getWx_name(), param.getWx_nick_name());
                    if (player != null) {
                        players.put(player.getId(), player);
                        wxToId.put(player.getWx_name(), player.getId());
                    }
                }
            } finally {
                lock.unlock();
            }
        } else {

        }

        LoginResult loginResult = new LoginResult();
        loginResult.setId(player.getIdStr());
        loginResult.setUser_nick_name(player.getWx_nick_name());
        loginResult.setToken(createPlayerToken(player));
        return loginResult;
    }

    @RequestMapping("/test")
    public Object test() {
        Map<String,Object> map = new HashMap<>();
        map.put("ly","??????");
        String token = creatToken(map);
        return token;
    }
    @RequestMapping("/test1")
    public Object test1(@RequestParam String token) {
        Util.instance.parseToken(token, jwtKey);
        return token;
    }

    @RequestMapping("/test2")
    public Object test2() {
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM, "inform.command", "hello rabbit");
        return "OK";
    }

    public String createPlayerToken(Player player) {
        Map<String,Object> map = new HashMap<>();
        map.put("id", player.getIdStr());
        map.put("nick_name", player.getWx_nick_name());
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


}
