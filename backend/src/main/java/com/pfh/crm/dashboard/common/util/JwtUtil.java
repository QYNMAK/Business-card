package com.pfh.crm.dashboard.common.util;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * JWT工具类
 */
public class JwtUtil {

    //有效期为 //60 * 60 * 1000 *36 *2 2天
    private static final Long JWT_TTL = 60 * 60 * 1000 *36 *2L;
    //设置秘钥明文
    private static final String JWT_KEY = "itcast";


    private static Cache<Object, Object> jwtCache = null;



    static {
        jwtCache = CacheBuilder.newBuilder().expireAfterWrite(JWT_TTL, TimeUnit.SECONDS).build();
    }

    /**
     * 创建token
     *
     * @param id        jwt id
     * @param subject   主题
     * @param ttlMillis 过期时间
     * @return jwt
     */
    public static String createJWT(String id, String subject, Long ttlMillis) {

        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        if (ttlMillis == null) {
            ttlMillis = JwtUtil.JWT_TTL;
        }
        long expMillis = nowMillis + ttlMillis;
        Date expDate = new Date(expMillis);
        SecretKey secretKey = generalKey();

        JwtBuilder builder = Jwts.builder()
                .setId(id)              //唯一的ID
                .setSubject(subject)   // 主题  可以是JSON数据
                .setIssuer("admin")     // 签发者
                .setIssuedAt(now)      // 签发时间
                .signWith(signatureAlgorithm, secretKey) //使用HS256对称加密算法签名, 第二个参数为秘钥
                .setExpiration(expDate);// 设置过期时间

        cacheJwt(id);

        return builder.compact();
    }

    /**
     * 生成加密后的秘钥 secretKey
     *
     * @return SecretKey
     */
    private static SecretKey generalKey() {
        byte[] encodedKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }


    /**
     * 解析
     *
     * @param jwt jwt
     * @return Claims
     * @throws Exception jwt 过期
     */
    public static Claims parseJWT(String jwt) throws Exception {
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(jwt)
                .getBody();
    }

    /**
     * 缓存jtw id 用于登出使用
     *
     * @param jid jwt id
     */
    private static void cacheJwt(String jid){
        jwtCache.put(jid,jid);
    }


    /**
     * 获取缓存数据
     *
     * @param jid 缓存key
     * @return 缓存数据，如果存在，否则返回null
     */
    public static boolean isExpiration(String jid) {
        return jwtCache.getIfPresent(jid) == null;
    }

    /**
     * 删除jwt
     *
     * @param jid jwt id
     */
    public static void delJwt(String jid) {
        jwtCache.invalidate(jid);
    }
}