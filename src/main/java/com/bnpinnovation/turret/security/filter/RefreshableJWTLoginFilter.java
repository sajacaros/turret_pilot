package com.bnpinnovation.turret.security.filter;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.dto.UserLogin;
import com.bnpinnovation.turret.dto.VerifyResult;
import com.bnpinnovation.turret.security.JWTUtil;
import com.bnpinnovation.turret.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class RefreshableJWTLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final AccountService accountService;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public RefreshableJWTLoginFilter(AuthenticationManager authenticationManager, AccountService accountService, JWTUtil jwtUtil, ObjectMapper objectMapper){
        this.authenticationManager = authenticationManager;
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UserLogin userLogin;
        try {
            userLogin = objectMapper.readValue(request.getInputStream(), UserLogin.class);
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }

        // id password login
        if(userLogin.isLogin()){
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userLogin.getUsername(), userLogin.getPassword()
            );
            return authenticationManager.authenticate(authToken);
        } else if(userLogin.isRefresh()){
            // refresh token
            if(!StringUtils.hasLength(userLogin.getRefreshToken()))
                throw new IllegalArgumentException("needed for refresh token : "+userLogin.getRefreshToken());

            VerifyResult result = jwtUtil.verify(userLogin.getRefreshToken());
            if(result.isResult()){
                Account account = accountService.getAccount(result.getUsername());
                return new UsernamePasswordAuthenticationToken(account.generateUserDetails(), null, account.roles());
            } else {
                throw new TokenExpiredException("refresh token expired");
            }
        } else {
            throw new IllegalArgumentException("not supported token type, type : "+userLogin.getType());
        }
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User)authResult.getPrincipal();
        response.addHeader(JWTUtil.AUTHENTICATION_HEADER, JWTUtil.BEARER + jwtUtil.generate(user.getUsername(), JWTUtil.TokenType.ACCESS));
        response.addHeader(JWTUtil.REFRESH_HEADER, jwtUtil.generate(user.getUsername(),  JWTUtil.TokenType.REFRESH));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.warn(failed.getMessage());
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
