package com.bnpinnovation.turret.security.filter;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.dto.UserLogin;
import com.bnpinnovation.turret.security.JWTUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JWTLoginFilter extends UsernamePasswordAuthenticationFilter {
    private final JWTUtil jwtUtil;
    private final ObjectMapper mapper;
    private final AuthenticationManager authenticationManager;

    public JWTLoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, ObjectMapper mapper) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.mapper = mapper;
        setFilterProcessesUrl("/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        UsernamePasswordAuthenticationToken authToken;
        try {
            UserLogin login = mapper.readValue(request.getInputStream(), UserLogin.class);
            log.info("{}", login);
            authToken = new UsernamePasswordAuthenticationToken(
                    login.getUsername(),login.getPassword(),null
            );
        } catch (IOException e) {
            throw new AuthenticationServiceException(e.getMessage());
        }
        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User)authResult.getPrincipal();
        response.addHeader("Authentication", "Bearer " + jwtUtil.generate(user.getUsername()));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.warn(failed.getMessage());
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
