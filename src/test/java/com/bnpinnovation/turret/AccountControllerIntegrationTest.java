package com.bnpinnovation.turret;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.dto.VerifyResult;
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
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AccountControllerIntegrationTest {

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
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private JWTUtil jwtUtil;

    private String adminUsername = "admin";
    private String adminRoleName = "ROLE_ADMIN";
    private String username = "user";
    private String roleName = "ROLE_USER";


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
    void test_jwt_login() throws URISyntaxException {
        Algorithm al = Algorithm.HMAC512("hello");
        Tokens tokens = jwtTokenTestHelper.getToken(username, username+"p");

        VerifyResult result = jwtUtil.verify(tokens.getAccessToken());
        assertTrue(result.isResult());

        DecodedJWT decodedAccessJWT = JWT.require(al).build().verify(tokens.getAccessToken());
        assertEquals(username, decodedAccessJWT.getClaims().get("sub").asString());
        assertNotNull(decodedAccessJWT.getId());
    }

    @DisplayName("2. admin으로 계정생성")
    @Test
    void test_create_account_by_admin() throws URISyntaxException {
        String testUsername = "aa";
        Tokens tokens = jwtTokenTestHelper.getToken(adminUsername, adminUsername+"p");
        Long userId = accountHelper.newAccount(tokens.getAccessToken(),testUsername,testUsername+"p",roleName);
        Optional<Account> searchedAccount = accountService.findAccount(userId);
        assertTrue(searchedAccount.isPresent());
        assertEquals(testUsername, searchedAccount.get().username());
    }

    @DisplayName("3. user 계정으로 계정생성")
    @Test
    void test_create_account_by_user() throws URISyntaxException {
        String testUsername = "aaa";
        Tokens tokens = jwtTokenTestHelper.getToken(username, username+"p");
        assertThrows(AuthorizationServiceException.class, ()->
            accountHelper.newAccount(tokens.getAccessToken(),testUsername,testUsername+"p", roleName,
                new ResponseErrorHandler(){
                    @Override
                    public boolean hasError(ClientHttpResponse response) {
                        return true;
                    }

                    @Override
                    public void handleError(ClientHttpResponse response) throws IOException {
                        if(HttpServletResponse.SC_FORBIDDEN == response.getRawStatusCode()) {
                            throw new AuthorizationServiceException(response.getStatusText());
                        }
                    }
                })
        );
    }
}
