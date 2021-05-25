package com.bnpinnovation.turret;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bnpinnovation.turret.helper.AccountTestHelper;
import com.bnpinnovation.turret.helper.JWTTokenTestHelper;
import com.bnpinnovation.turret.helper.Tokens;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RefreshTokenTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private AccountTestHelper accountHelper;
    private JWTTokenTestHelper jwtTokenTestHelper;

    @LocalServerPort
    private int port;

    private String adminUsername = "admin";
    private String adminRoleName = "ADMIN";
    private String username = "user";
    private String roleName = "USER";

    @BeforeEach
    public void before() {
        if( this.accountHelper == null) {
            this.accountHelper = new AccountTestHelper(accountService, roleService, passwordEncoder, port);
        }
        if( this.jwtTokenTestHelper == null ) {
            this.jwtTokenTestHelper = new JWTTokenTestHelper(port);
        }
        accountHelper.createUserAndRole(adminUsername, adminRoleName);
        accountHelper.createUserAndRole(username, roleName);
    }

    @DisplayName("1. refresh token으로 access token 얻기")
    @Test
    void test_refresh() throws URISyntaxException {
        Algorithm al = Algorithm.HMAC512("hello");
        final Tokens tokens = jwtTokenTestHelper.getToken(username, username+"p");
        final Tokens reTokens = jwtTokenTestHelper.getRefreshToken(tokens.getRefreshToken());
        Assertions.assertNotEquals(tokens.getAccessToken(), reTokens.getAccessToken());
        Assertions.assertNotEquals(tokens.getRefreshToken(), reTokens.getRefreshToken());
    }
}
