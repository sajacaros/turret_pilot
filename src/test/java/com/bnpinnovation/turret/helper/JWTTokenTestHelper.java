package com.bnpinnovation.turret.helper;

import com.auth0.jwt.interfaces.Claim;
import com.bnpinnovation.turret.dto.UserLogin;
import com.bnpinnovation.turret.security.JWTUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JWTTokenTestHelper {
    private final int port;

    protected RestTemplate restTemplate = new RestTemplate();

    public JWTTokenTestHelper(int port) {
        this.port = port;
    }

    private URI uri(String path) throws URISyntaxException {
        return new URI(String.format("http://localhost:%d%s", port, path));
    }

    public Tokens getToken(String username, String password) throws URISyntaxException {
        return getToken(username,password,null);
    }

    public Tokens getToken(String username, String password, ResponseErrorHandler errorHandler) throws URISyntaxException {
        UserLogin login = UserLogin.builder().username(username).password(password).type(UserLogin.Type.LOGIN).build();
        HttpEntity<UserLogin> body = new HttpEntity<>(login);
        if(errorHandler!=null) {
            restTemplate.setErrorHandler(errorHandler);
        }
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"), HttpMethod.POST, body, String.class);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getHeaders().get(JWTUtil.REFRESH_HEADER));
        return new Tokens(
                response.getHeaders().get(JWTUtil.REFRESH_HEADER).get(0),
                response.getHeaders().get(JWTUtil.AUTHENTICATION_HEADER).get(0).substring(JWTUtil.BEARER.length())
        );
    }

    public Tokens getRefreshToken(String refreshToken) throws URISyntaxException {
        UserLogin login = UserLogin.builder().type(UserLogin.Type.REFRESH)
                .refreshToken(refreshToken).build();
        HttpEntity<UserLogin> body = new HttpEntity<>(login);
        ResponseEntity<String> response = restTemplate.exchange(uri("/login"),
                HttpMethod.POST, body, String.class);
        return new Tokens(
                response.getHeaders().get(JWTUtil.REFRESH_HEADER).get(0),
                response.getHeaders().get(JWTUtil.AUTHENTICATION_HEADER).get(0).substring(JWTUtil.BEARER.length())
        );
    }

    public static void printClaim(String key, Claim value){
        if(value.isNull()){
            System.out.printf("%s:%s\n", key, "none");
            return;
        }
        if(value.asString() != null){
            System.out.printf("%s:{str}%s\n", key, value.asString());
            return;
        }
        if(value.asLong() != null){
            System.out.printf("%s:{lng}%d\n", key, value.asLong());
            return;
        }
        if(value.asInt() != null ){
            System.out.printf("%s:{int}%d\n", key, value.asInt());
            return;
        }
        if(value.asBoolean() != null){
            System.out.printf("%s:{bol}%b\n", key, value.asBoolean());
            return;
        }
        if(value.asDate() != null){
            System.out.printf("%s:{dte}%s\n", key, value.asDate().toString());
            return;
        }
        if(value.asDouble() != null){
            System.out.printf("%s:{dbl}%f\n", key, value.asDouble());
            return;
        }
        String[] values = value.asArray(String.class);
        if(values != null){
            System.out.printf("%s:{arr}%s\n", key, Stream.of(values).collect(Collectors.joining(",")));
            return;
        }
        Map valueMap = value.asMap();
        if(valueMap != null) {
            System.out.printf("%s:{map}%s\n", key, valueMap);
            return;
        }
        System.out.println("====>> unknown type for :"+key);
    }
}
