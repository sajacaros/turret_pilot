package com.bnpinnovation.turret;

import com.bnpinnovation.turret.dto.UserLogin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;

import java.net.URI;
import java.net.URISyntaxException;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JWTLoginFilterTest {

    @LocalServerPort
    private int port;

    private URI uri(String path) throws URISyntaxException {
        return new URI(String.format("http://localhost:%d%s", port, path));
    }

    @DisplayName("1. jwt로 로그인 시도")
    @Test
    void test_jwtLogin() {
        UserLogin login = UserLogin.builder().username("hello").password("test1234").build();
//        HttpEntity<UserLogin> bo
    }
}
