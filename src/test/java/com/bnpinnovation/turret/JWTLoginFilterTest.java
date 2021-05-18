package com.bnpinnovation.turret;

import ch.qos.logback.core.net.SyslogOutputStream;
import com.bnpinnovation.turret.domain.Account;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JWTLoginFilterTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleService roleService;

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
    void before() {
        this.accountHelper = new AccountTestHelper(accountService,roleService);
        accountHelper.createUserAndRole(adminUsername,adminRoleName);
        accountHelper.createUserAndRole(username,roleName);
    }

    @DisplayName("1. jwt로 로그인 시도")
    @Test
    void test_jwtLogin() throws URISyntaxException {
       String accessToken = getToken("hello", "test");

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
