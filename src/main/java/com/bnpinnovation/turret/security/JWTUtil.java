package com.bnpinnovation.turret.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bnpinnovation.turret.dto.VerifyResult;

import java.time.Instant;

public class JWTUtil {

    private String secret = "hello";
    private Algorithm al = Algorithm.HMAC512(secret);
    private long lifeTime = 30; // 30s

    public String generate(String username) {
        return JWT.create()
                .withSubject(username)
                .withClaim("exp", Instant.now().getEpochSecond()+lifeTime)
                .sign(al);
    }
    public VerifyResult verify(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(al).build().verify(token);
            return VerifyResult.builder().username(decodedJWT.getSubject()).result(true).build();
        } catch (JWTVerificationException ex) {
            DecodedJWT decodedJWT = JWT.decode(token);
            return VerifyResult.builder().username(decodedJWT.getSubject()).result(false).build();
        }
    }
}
