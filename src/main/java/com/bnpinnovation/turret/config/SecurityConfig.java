package com.bnpinnovation.turret.config;

import com.bnpinnovation.turret.dto.ErrorMessage;
import com.bnpinnovation.turret.security.JWTUtil;
import com.bnpinnovation.turret.security.filter.JWTCheckFilter;
import com.bnpinnovation.turret.security.filter.JWTLoginFilter;
import com.bnpinnovation.turret.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@Slf4j
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AccountService userDetailsService;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
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
        return new JWTLoginFilter(authenticationManager(), jwtUtil(), mapper);
    }

    public Filter authorizationFilter() throws Exception {
        return new JWTCheckFilter(authenticationManager(), userDetailsService, jwtUtil());
    }

//    @Bean
//    UserDetailsService users() {
//        UserDetails user1 = User.builder()
//                .username("user1")
//                .password(passwordDecoder().encode( "1234"))
//                .roles("USER")
//                .build();
//        UserDetails admin = User.builder()
//                .username("admin")
//                .password(passwordDecoder().encode("12345"))
//                .roles("ADMIN")
//                .build();
//        return new InMemoryUserDetailsManager(user1, admin);
//    }

    @Bean
    PasswordEncoder passwordDecoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    JWTUtil jwtUtil() {
        return new JWTUtil();
    }
}
