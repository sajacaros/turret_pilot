package com.bnpinnovation.turret;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bnpinnovation.turret.dto.UserLogin;
import com.bnpinnovation.turret.helper.AccountTestHelper;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
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

    @LocalServerPort
    private int port;
    @Autowired
    private RestTemplate restTemplate;

    private String adminUsername = "admin";
    private String adminRoleName = "ADMIN";
    private String username = "user";
    private String roleName = "USER";

    @BeforeEach
    public void before() {
        if( this.accountHelper == null) {
            this.accountHelper = new AccountTestHelper(accountService, roleService, passwordEncoder);
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
        Algorithm al = Algorithm.HMAC512("hello");
        String accessToken = getToken(username, username+"p", null);

        DecodedJWT decodedJWT = JWT.require(al).build().verify(accessToken);
        assertEquals(username, decodedJWT.getClaims().get("sub").asString());
    }

    @DisplayName("2. 비번 틀림 시도")
    @Test
    void test_jwtLogin_failed() {
        assertThrows(AuthenticationServiceException.class, ()->
                getToken(username, username,
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

    private URI uri(String path) throws URISyntaxException {
        return new URI(String.format("http://localhost:%d%s", port, path));
    }

    private String getToken(String username, String password, ResponseErrorHandler errorHandler) throws URISyntaxException {
        UserLogin login = UserLogin.builder().username(username).password(password).build();
        HttpEntity<UserLogin> body = new HttpEntity<>(login);
        if(errorHandler!=null) {
            restTemplate.setErrorHandler(errorHandler);
        }
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);
        assertEquals(200, response.getStatusCodeValue());
        return response.getHeaders().get(JWTUtil.AUTHENTICATION).get(0).substring(JWTUtil.BEARER.length());
    }
}
