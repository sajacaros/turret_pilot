package com.bnpinnovation.turret;

import com.bnpinnovation.turret.dto.ThirdForm;
import com.bnpinnovation.turret.helper.AccountTestHelper;
import com.bnpinnovation.turret.helper.JWTTokenTestHelper;
import com.bnpinnovation.turret.security.JWTUtil;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import com.bnpinnovation.turret.service.ThirdService;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ThirdControllerIntegrationTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ThirdService thirdService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private AccountTestHelper accountHelper;
    private JWTTokenTestHelper jwtTokenTestHelper;

    @LocalServerPort
    private int port;

    private RestTemplate restTemplate = new RestTemplate();

    private String adminUsername = "admin";
    private String adminRoleName = "ROLE_ADMIN";
    private String adminAccessToken;


    @BeforeEach
    public void before() throws URISyntaxException {
        if (this.accountHelper == null) {
            this.accountHelper = new AccountTestHelper(accountService, roleService, passwordEncoder, port);
        }
        if (this.jwtTokenTestHelper == null) {
            this.jwtTokenTestHelper = new JWTTokenTestHelper(port);
        }
        accountHelper.createUserAndRole(adminUsername, adminRoleName);
        adminAccessToken = jwtTokenTestHelper.getToken(adminUsername, adminUsername + "p").getAccessToken();
    }

    @AfterEach
    void after() {
        accountService.removeAll();
        thirdService.removeAll();
    }


    @DisplayName("1. third key 생성")
    @Test
    public void test_create_third_key() throws URISyntaxException {
        String symbol = "symbol";
        String name = "name";
        Long lifetime = 86400L;
        newThird(symbol, name, lifetime);
    }

    @DisplayName("2. third key refresh")
    @Test
    public void test_refresh_third_key() throws URISyntaxException {
        String symbol = "symbol";
        String name = "name";
        Long lifetime = 86400L;
        ThirdForm.ThirdDetails details = newThird(symbol, name, lifetime);
        ThirdForm.ThirdDetails refreshedDetails = refreshThird(details.getThirdId());
        assertNotEquals(details.getAccessToken(), refreshedDetails.getAccessToken());
    }

    @DisplayName("3. third key disable")
    @Test
    public void test_disable_third() throws URISyntaxException {
        String symbol = "symbol";
        String name = "name";
        Long lifetime = 86400L;
        ThirdForm.ThirdDetails details = newThird(symbol, name, lifetime);
        disableThird(details.getThirdId());
        ThirdForm.ThirdDetails[] detailsList = findAll();
        for(ThirdForm.ThirdDetails d: detailsList) {
            assertFalse(d.isEnabled());
        }
    }

    @DisplayName("4. third key enable")
    @Test
    public void test_enable_third() throws URISyntaxException {
        String symbol = "symbol";
        String name = "name";
        Long lifetime = 86400L;
        ThirdForm.ThirdDetails details = newThird(symbol, name, lifetime);
        disableThird(details.getThirdId());
        enableThird(details.getThirdId());
        ThirdForm.ThirdDetails[] detailsList = findAll();
        for(ThirdForm.ThirdDetails d: detailsList) {
            assertTrue(d.isEnabled());
        }
    }

    @DisplayName("5. third access")
    @Test
    public void test_access_third() throws URISyntaxException {
        String symbol = "symbol";
        String name = "name";
        Long lifetime = 86400L;
        ThirdForm.ThirdDetails details = newThird(symbol, name, lifetime);
        ThirdForm.ThirdDetails identityDetails = identity(details.getAccessToken(), new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return true;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
                if (HttpServletResponse.SC_FORBIDDEN == response.getRawStatusCode()) {
                    throw new AuthorizationServiceException(response.getStatusText());
                }
            }
        });
        assertEquals(details.getThirdId(), identityDetails.getThirdId());
    }

    @DisplayName("6. third access by master")
    @Test
    public void test_access_third_by_master() {
        assertThrows(AuthorizationServiceException.class, ()->
                identity(adminAccessToken, new ResponseErrorHandler(){
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

    private ThirdForm.ThirdDetails identity(String thirdAccessToken) throws URISyntaxException {
        return identity(thirdAccessToken, null);
    }

    private ThirdForm.ThirdDetails identity(String thirdAccessToken, ResponseErrorHandler errorHandler) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTHENTICATION_HEADER, JWTUtil.BEARER+thirdAccessToken);
        HttpEntity<ThirdForm.New> body = new HttpEntity<>(null,headers);
        if(errorHandler != null) {
            restTemplate.setErrorHandler(errorHandler);
        }
        ResponseEntity<ThirdForm.ThirdDetails> response = restTemplate.exchange(uri("/third/identity"), HttpMethod.POST, body, ThirdForm.ThirdDetails.class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCodeValue());
        return response.getBody();
    }

    private ThirdForm.ThirdDetails[] findAll() throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTHENTICATION_HEADER, JWTUtil.BEARER+adminAccessToken);
        HttpEntity<ThirdForm.New> body = new HttpEntity<>(null,headers);
        ResponseEntity<ThirdForm.ThirdDetails[]> response = restTemplate.exchange(uri("/third/all"), HttpMethod.GET, body, ThirdForm.ThirdDetails[].class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCodeValue());
        return response.getBody();
    }

    private void enableThird(Long thirdId) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTHENTICATION_HEADER, JWTUtil.BEARER+adminAccessToken);
        HttpEntity<ThirdForm.New> body = new HttpEntity<>(null,headers);
        ResponseEntity<ThirdForm.ThirdDetails> response = restTemplate.exchange(uri("/third/"+thirdId+"/enable"), HttpMethod.POST, body, ThirdForm.ThirdDetails.class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCodeValue());
    }

    private void disableThird(Long thirdId) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTHENTICATION_HEADER, JWTUtil.BEARER+adminAccessToken);
        HttpEntity<ThirdForm.New> body = new HttpEntity<>(null,headers);
        ResponseEntity<ThirdForm.ThirdDetails> response = restTemplate.exchange(uri("/third/"+thirdId+"/disable"), HttpMethod.POST, body, ThirdForm.ThirdDetails.class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCodeValue());
    }

    private URI uri(String path) throws URISyntaxException {
        return new URI(String.format("http://localhost:%d%s", port, path));
    }

    private ThirdForm.ThirdDetails newThird(String symbol, String name, Long lifetime) throws URISyntaxException {
        ThirdForm.New thirdForm = ThirdForm.New.builder()
                .symbol(symbol)
                .name(name)
                .lifeTime(lifetime)
                .build();
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTHENTICATION_HEADER, JWTUtil.BEARER+adminAccessToken);
        HttpEntity<ThirdForm.New> body = new HttpEntity<>(thirdForm,headers);
        ResponseEntity<ThirdForm.ThirdDetails> response = restTemplate.exchange(uri("/third"), HttpMethod.POST, body, ThirdForm.ThirdDetails.class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCodeValue());
        assertDetails(response.getBody(), symbol, name, lifetime);
        return response.getBody();
    }

    private ThirdForm.ThirdDetails refreshThird(Long thirdId) throws URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTHENTICATION_HEADER, JWTUtil.BEARER+adminAccessToken);
        HttpEntity<ThirdForm.New> body = new HttpEntity<>(null,headers);
        ResponseEntity<ThirdForm.ThirdDetails> response = restTemplate.exchange(uri("/third/"+thirdId+"/refresh"), HttpMethod.POST, body, ThirdForm.ThirdDetails.class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCodeValue());
        return response.getBody();
    }

    private void assertDetails(ThirdForm.ThirdDetails details, String symbol, String name, Long lifetime) {
        assertEquals(symbol, details.getSymbol());
        assertEquals(name, details.getName());
        assertEquals(lifetime, details.getLifeTime());
        assertNotNull(details.getAccessToken());
        assertTrue(details.isEnabled());
        assertNotNull(details.getExpiredDate());
    }
}