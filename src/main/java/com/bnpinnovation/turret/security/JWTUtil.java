package com.bnpinnovation.turret.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bnpinnovation.turret.dto.VerifyResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;

@Component
public class JWTUtil {

    public static final String AUTHENTICATION_HEADER = "Authentication";
    public static final String REFRESH_HEADER = "refresh-token";
    public static final String BEARER = "Bearer ";
    public static final String ORIGIN = "origin";
    private final String secret = "hello";
    private final Algorithm al = Algorithm.HMAC512(secret);
    @Value("${life.time.access:30}") // 30s
    private Long accessLifeTime_s;
    @Value("${life.time.access:3600}") // 1h, 60*60s
    private Long refreshLifeTime_s;

    public enum TokenType {
        ACCESS(Origin.ORIGIN),
        REFRESH(Origin.ORIGIN),
        THIRD(Origin.THIRD);

        Origin origin;

        TokenType(Origin origin) {
            this.origin = origin;
        }

        public enum Origin {
            ORIGIN, THIRD;

            public boolean isOrigin() {
                return ORIGIN.equals(this);
            }
        }


        public Origin origin() {
            return this.origin;
        }

        public boolean isOrigin() {
            return this.origin.isOrigin();
        }
    }

    public String generate(String username, TokenType tokenType) {
        return generate(username, tokenType, getLifeTime(tokenType));
    }

    public String generate(String username, TokenType tokenType, long lifeTime_s) {
        return JWT.create()
                .withSubject(username)
                .withClaim("exp", Instant.now().getEpochSecond()+lifeTime_s)
                .withClaim(ORIGIN, tokenType.origin().name())
                .withJWTId(UUID.randomUUID().toString())
                .sign(al);
    }

    private long getLifeTime(TokenType tokenType) {
        switch (tokenType) {
            case REFRESH:
                return this.refreshLifeTime_s;
            case ACCESS:
            default:
                return this.accessLifeTime_s;
        }
    }

    public VerifyResult verify(String token) {
        try {
            DecodedJWT decodedJWT = JWT.require(al).build().verify(token);
            return VerifyResult.builder()
                    .username(decodedJWT.getSubject())
                    .origin(TokenType.Origin.ORIGIN.valueOf(decodedJWT.getClaim(ORIGIN).asString()))
                    .result(true).build();
        } catch (JWTVerificationException ex) {
            DecodedJWT decodedJWT = JWT.decode(token);
            return VerifyResult.builder().username(decodedJWT.getSubject()).result(false).build();
        }
    }
}
