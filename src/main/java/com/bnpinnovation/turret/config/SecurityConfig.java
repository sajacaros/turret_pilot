package com.bnpinnovation.turret.config;

import com.bnpinnovation.turret.dto.ErrorMessage;
import com.bnpinnovation.turret.security.JWTUtil;
import com.bnpinnovation.turret.security.filter.JWTCheckFilter;
import com.bnpinnovation.turret.security.filter.RefreshableJWTLoginFilter;
import com.bnpinnovation.turret.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.Filter;
import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccountService accountService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private JWTUtil jwtUtil;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(accountService)
                .passwordEncoder(passwordDecoder());
    }

    @Autowired
    ObjectMapper objectMapper;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilter(authenticationFilter())
                .addFilter(authorizationFilter())
                .exceptionHandling()
                    .authenticationEntryPoint((request, response, authenticationException) -> {
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.getWriter().write(objectMapper.writeValueAsString(new ErrorMessage(1L,authenticationException.getMessage())));
                        SecurityContextHolder.clearContext();

                    })
                    .accessDeniedHandler((request, response, accessDeniedException) -> {
                        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        response.getWriter().write(objectMapper.writeValueAsString(new ErrorMessage(2L,accessDeniedException.getMessage())));
                        SecurityContextHolder.clearContext();
                    })
                .and()
                .authorizeRequests()
                    .antMatchers("/h2-console/**").permitAll()
                    .antMatchers("/login").permitAll()
                .and()
                    .headers()
                        .frameOptions().sameOrigin()
        ;
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    public Filter authenticationFilter() throws Exception {
        return new RefreshableJWTLoginFilter(authenticationManager(), accountService, jwtUtil, mapper);
    }

    public Filter authorizationFilter() throws Exception {
        return new JWTCheckFilter(authenticationManager(), accountService, jwtUtil);
    }


    @Bean
    PasswordEncoder passwordDecoder() {
        return new BCryptPasswordEncoder();
    }
}
