package com.bnpinnovation.turret;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.dto.UserLogin;
import com.bnpinnovation.turret.helper.AccountTestHelper;
import com.bnpinnovation.turret.helper.JWTTokenTestHelper;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
        if(!accountService.existUserName(adminUsername)) {
            accountHelper.createUserAndRole(adminUsername, adminRoleName);
        }
        if(!accountService.existUserName(username)) {
            accountHelper.createUserAndRole(username, roleName);
        }
    }

    @DisplayName("1. 사용자 조회")
    @Test
    void test_user_create() {
        Optional<Account> searchedAccountOptional = accountService.findAccount(1L);
        assertTrue(searchedAccountOptional.isPresent());
        assertEquals(adminUsername, searchedAccountOptional.get().username());
    }

    @DisplayName("2. jwt로 로그인 시도")
    @Test
    void test_jwtLogin() throws URISyntaxException {
        Algorithm al = Algorithm.HMAC512("hello");
        String accessToken = getToken(username, username+"p");

        DecodedJWT decodedJWT = JWT.require(al).build().verify(accessToken);
        assertEquals(username, decodedJWT.getClaims().get("sub").asString());
    }

    private URI uri(String path) throws URISyntaxException {
        return new URI(String.format("http://localhost:%d%s", port, path));
    }

    private String getToken(String username, String password) throws URISyntaxException {
        UserLogin login = UserLogin.builder().username(username).password(password).build();
        HttpEntity<UserLogin> body = new HttpEntity<>(login);
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);
        assertEquals(200, response.getStatusCodeValue());
        return response.getHeaders().get("Authentication").get(0).substring("Bearer ".length());
    }
}
