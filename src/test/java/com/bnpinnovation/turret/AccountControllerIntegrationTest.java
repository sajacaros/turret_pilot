package com.bnpinnovation.turret;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.dto.AccountForm;
import com.bnpinnovation.turret.dto.UserLogin;
import com.bnpinnovation.turret.helper.AccountTestHelper;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerIntegrationTest {

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


    @DisplayName("1. jwt로 로그인 시도")
    @Test
    void test_jwt_login() throws URISyntaxException {
        Algorithm al = Algorithm.HMAC512("hello");
        String accessToken = getToken(username, username+"p");

        DecodedJWT decodedJWT = JWT.require(al).build().verify(accessToken);
        assertEquals(username, decodedJWT.getClaims().get("sub").asString());
    }

    @DisplayName("2. admin으로 계정생성")
    @Test
    void test_create_account_by_admin() throws URISyntaxException {
        String accessToken = getToken(adminUsername, adminUsername+"p");
        Long id = newAccount(accessToken,"aa","bb","USER");
        System.out.println("created user id : "+ id);
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

    private Long newAccount(String token, String name, String password, String roleName) throws URISyntaxException {
        AccountForm.NewAccount newAccount = AccountForm.NewAccount.builder()
                .username(name)
                .password(password)
                .name(name+"d")
                .roles(Arrays.asList(roleName))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authentication", "Bearer "+token);
        HttpEntity<AccountForm.NewAccount> body = new HttpEntity<>(newAccount,headers);
        ResponseEntity<Long> response = restTemplate.exchange(uri("/scv/account"), HttpMethod.POST, body, Long.class);
        assertEquals(200, response.getStatusCodeValue());
        return response.getBody();
    }
}
