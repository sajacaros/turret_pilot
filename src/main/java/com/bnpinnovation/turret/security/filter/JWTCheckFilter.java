package com.bnpinnovation.turret.security.filter;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.domain.Third;
import com.bnpinnovation.turret.dto.VerifyResult;
import com.bnpinnovation.turret.security.JWTUtil;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.ThirdService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
public class JWTCheckFilter extends BasicAuthenticationFilter {
    private final JWTUtil jwtUtil;
    private final AccountService accountService;
    private final ThirdService thirdService;

    public JWTCheckFilter(AuthenticationManager authenticationManager, AccountService accountService, ThirdService thirdService, JWTUtil jwtUtil) {
        super(authenticationManager);
        this.accountService = accountService;
        this.thirdService = thirdService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String tokenWithBearer = request.getHeader(JWTUtil.AUTHENTICATION_HEADER);
        if(tokenWithBearer == null || !tokenWithBearer.startsWith(JWTUtil.BEARER)) {
            chain.doFilter(request,response);
            return;
        }
        String token = tokenWithBearer.substring(JWTUtil.BEARER.length());
        VerifyResult result = jwtUtil.verify(token);
        if(result.isResult()) {
            switch (result.getOrigin()) {
                case THIRD:
                    Third third = thirdService.getThird(result.getUsername());
                    if(third.valid(token)) {
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(third.generateUserDetails(), null, third.roles())
                        );
                    }
                    break;
                case ORIGIN:
                default:
                    Account account = accountService.getAccount(result.getUsername());
                    SecurityContextHolder.getContext().setAuthentication(
                            new UsernamePasswordAuthenticationToken(account.generateUserDetails(), null, account.roles())
                    );
            }
        }

        super.doFilterInternal(request, response, chain);
    }
}
