//package com.wang.fastfoodapi.Util;
//
//import com.wang.fastfoodapi.Exception.InvalidJwtAuthenticationException;
//import io.jsonwebtoken.*;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.AuthorityUtils;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.stereotype.Component;
//
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @Auther: wAnG
// * @Date: 2022/1/3 23:49
// * @Description:
// */
//
//@Component
//public class JwtUtils {
//
//    private static long expireTime;
//    private static String secretKey;
//    private static final String CLAIM_KEY_USERNAME = "sub";
//    private static final String CLAIM_KEY_CREATED = "created";
//    private static final String AUTHORITIES_KEY = "roles";
//
//    @Value("${token.secretKey}")
//    public void setSecretKey(String secretKey) {
//        JwtUtils.secretKey = secretKey;
//    }
//
//    @Value("${token.expireTime}")
//    public void setExpireTime(long expireTime) {
//        JwtUtils.expireTime = expireTime;
//    }
//
//    /**
//     * 判断token是否存在
//     *
//     */
//    public static boolean judgeTokenIsExist(String token) {
//        return token != null && !"".equals(token) && !"null".equals(token);
//    }
//
//    public boolean validateToken(String token) {
//        try {
//            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
//            return !claims.getBody().getExpiration().before(new Date());
//        } catch (JwtException | IllegalArgumentException e) {
//            throw new InvalidJwtAuthenticationException("Expired or invalid JWT token");
//        }
//    }
//
//    public static Authentication getAuthentication(String token){
//        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
//
//        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);
//
//        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
//                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());
//
//        UserDetails principal = new User(claims.getSubject(), "", authorities);
//
//        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
//    }
//
//
//    /**
//     * 根据用户信息生成token
//     *
//     */
//    public static String generateToken(UserDetails userDetails) {
//        Map<String, Object> claims = new HashMap<>();
//        claims.put(CLAIM_KEY_USERNAME, userDetails.getUsername());
//        claims.put(CLAIM_KEY_CREATED, new Date());
//        return generateToken(claims);
//    }
//
//    /**
//     * 根据荷载生成JWT TOKEN
//     *
//     */
//    private static String generateToken(Map<String, Object> claims) {
//        return Jwts.builder()
//                .setClaims(claims)
//                .setExpiration(generateExpirationDate())
//                .signWith(SignatureAlgorithm.HS512, secretKey)
//                .compact();
//    }
//
//    /**
//     * 生成token失效时间
//     *
//     */
//    private static Date generateExpirationDate() {
//        return new Date(System.currentTimeMillis() + expireTime * 1000);
//    }
//
//    /**
//     * 从token中获取过期时间
//     */
//    private static Date getExpiredDateFromToken(String token) {
//        Claims claims = getClaimsFormToken(token);
//        return claims.getExpiration();
//    }
//
//    /**
//     * 从token中获取荷载
//     */
//    private static Claims getClaimsFormToken(String token) {
//        Claims claims = null;
//        try {
//            claims = Jwts.parser()
//                    .setSigningKey(secretKey)
//                    .parseClaimsJws(token)
//                    .getBody();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return claims;
//    }
//
//    /**
//     * 判断token是否失效
//     */
//    private static boolean isTokenExpired(String token) {
//        Date expireDate = getExpiredDateFromToken(token);
//        return expireDate.before(new Date());
//    }
//
//    /**
//     * 刷新token
//     * @param token
//     * @return
//     */
//    public String refreshToken(String token){
//        Claims claims = getClaimsFormToken(token);
//        claims.put(CLAIM_KEY_CREATED,new Date());
//        return generateToken(claims);
//    }
//
//    /**
//     * 判断token是否可以被刷新
//     * @param token
//     * @return
//     */
//    public boolean canRefresh(String token){
//        return !isTokenExpired(token);
//    }
//
//    /**
//     * 验证token是否有效
//     * @param token
//     * @param userDetails
//     * @return
//     */
//    public static boolean validateToken(String token,UserDetails userDetails){
//        String username = getUserNameFromToken(token);
//        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
//    }
//
//    /**
//     * 从token中获取登录用户名
//     * @param token
//     * @return
//     */
//    public static String getUserNameFromToken(String token){
//        String username;
//        try {
//            Claims claims = getClaimsFormToken(token);
//            username = claims.getSubject();
//        } catch (Exception e) {
//            username = null;
//        }
//        return username;
//    }
//
//    /**
//     * 生成token
//     *
//     * @param subject
//     * @return
//     */
//    public static String generateToken(String subject) {
//        String jwt = Jwts.builder()
//                .setSubject(subject)
//                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
//                .signWith(SignatureAlgorithm.HS512, secretKey)
//                .compact();
//        return jwt;
//    }
//
//    /**
//     * 生成带角色权限的token
//     *
//     * @param subject
//     * @param authorities
//     * @return
//     */
//    public static String generateToken(String subject, Collection<?> authorities) {
//        StringBuilder sb = new StringBuilder();
////        for (GrantedAuthority authority : authorities) {
////            sb.append(authority.getAuthority()).append(",");
////        }
//        String jwt = Jwts.builder()
//                .setSubject(subject)
//                .claim("authorities", sb)
//                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
//                .signWith(SignatureAlgorithm.HS512, secretKey)
//                .compact();
//        return jwt;
//    }
//
//    /**
//     * 生成自定义过期时间token
//     *
//     * @param subject
//     * @param expireTime
//     * @return
//     */
//    public static String generateToken(String subject, long expireTime) {
//        String jwt = Jwts.builder()
//                .setSubject(subject)
//                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
//                .signWith(SignatureAlgorithm.HS512, secretKey)
//                .compact();
//        return jwt;
//    }
//
//
//    /**
//     * 获取tokenBody同时校验token是否有效（无效则会抛出异常）
//     *
//     * @param token
//     * @return
//     */
//    public static Claims getTokenBody(String token) {
//        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token.replace("Bearer", "")).getBody();
//        return claims;
//    }
//}
//
