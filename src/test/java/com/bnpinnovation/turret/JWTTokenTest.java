package com.bnpinnovation.turret;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bnpinnovation.turret.helper.JWTTokenTestHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JWTTokenTest {

    @Test
    @DisplayName("1. JWT 토큰 만들기")
    public void test_jwt_create() {
        Algorithm al = Algorithm.HMAC512("hello");
        String subject = "hello";
        Long expiredEpoch = Instant.now().getEpochSecond() + 3;
        String[] roles = new String[]{"ROLE_ADMIN", "ROLE_USER"};

        String token = JWT.create()
                .withSubject(subject)
//                .withExpiresAt(new Date())
                .withClaim("exp", expiredEpoch) // after 3s
                .withArrayClaim("role", roles)
                .sign(al);
        System.out.println(token);
        DecodedJWT decodedJWT = JWT.require(al).build().verify(token);
//        DecodedJWT decodedJWT = JWT.decode(token);
        JWTTokenTestHelper.printClaim("typ", decodedJWT.getHeaderClaim("typ"));
        assertEquals("JWT", decodedJWT.getHeaderClaim("typ").asString());
        JWTTokenTestHelper.printClaim("alg", decodedJWT.getHeaderClaim("alg"));
        assertEquals(al.getName(), decodedJWT.getHeaderClaim("alg").asString());

        decodedJWT.getClaims().forEach(JWTTokenTestHelper::printClaim);
        assertEquals(expiredEpoch, decodedJWT.getClaims().get("exp").asLong());
        assertEquals(subject, decodedJWT.getClaims().get("sub").asString());
        assertArrayEquals(roles, decodedJWT.getClaims().get("role").asArray(String.class));
    }

    @Test
    @DisplayName("2. JWT 토큰 expired")
    public void test_jwt_expired() throws InterruptedException {
        Algorithm al = Algorithm.HMAC512("hello");

        String token = JWT.create()
                .withSubject("hello")
//                .withExpiresAt(new Date())
                .withClaim("exp", Instant.now().getEpochSecond() + 1) // after 1s
                .withArrayClaim("role", new String[]{"ROLE_ADMIN", "ROLE_USER"})
                .sign(al);
        Thread.sleep(1500); // dummy

        Assertions.assertThrows(TokenExpiredException.class, () -> {
            JWT.require(al).build().verify(token);
        });
    }
}
