package com.bnpinnovation.turret.config;

import com.bnpinnovation.turret.security.JWTUtil;
import com.bnpinnovation.turret.security.filter.JWTLoginFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.Filter;

@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper mapper;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordDecoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilter(usernamePasswordFilter())
                .authorizeRequests()
                    .antMatchers("/h2-console/**").permitAll()
                .and()
                    .headers()
                        .frameOptions().sameOrigin()
        ;
    }

    @Override
    protected AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    public Filter usernamePasswordFilter() throws Exception {
        return new JWTLoginFilter(authenticationManager(), jwtUtil(), mapper);
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
