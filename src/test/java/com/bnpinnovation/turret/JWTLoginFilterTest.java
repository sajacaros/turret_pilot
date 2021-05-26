package com.bnpinnovation.turret;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bnpinnovation.turret.helper.AccountTestHelper;
import com.bnpinnovation.turret.helper.JWTTokenTestHelper;
import com.bnpinnovation.turret.helper.Tokens;
import com.bnpinnovation.turret.security.JWTUtil;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResponseErrorHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JWTLoginFilterTest {

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

    @AfterEach
    void after() {
        accountService.removeAll();
    }


    @DisplayName("1. jwt로 로그인 시도")
    @Test
    void test_jwtLogin() throws URISyntaxException {
        Algorithm al = Algorithm.HMAC512(JWTUtil.SECRET);
        Tokens tokens = jwtTokenTestHelper.getToken(username, username+"p");

        DecodedJWT decodedJWT = JWT.require(al).build().verify(tokens.getAccessToken());
        assertEquals(username, decodedJWT.getClaims().get("sub").asString());
    }

    @DisplayName("2. 비번 틀림 시도")
    @Test
    void test_jwtLogin_failed() {
        assertThrows(AuthenticationServiceException.class, ()->
                jwtTokenTestHelper.getToken(username, username,
                new ResponseErrorHandler(){
                    @Override
                    public boolean hasError(ClientHttpResponse response) {
                        return true;
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        if(HttpServletResponse.SC_UNAUTHORIZED == response.getRawStatusCode()) {
                            throw new AuthenticationServiceException(response.getStatusText());
                        }
                    }
                })
        );
    }
}
