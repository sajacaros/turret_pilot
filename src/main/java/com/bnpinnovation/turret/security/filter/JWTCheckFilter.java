package com.bnpinnovation.turret.security.filter;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.dto.VerifyResult;
import com.bnpinnovation.turret.security.JWTUtil;
import com.bnpinnovation.turret.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JWTCheckFilter extends BasicAuthenticationFilter {
    private final JWTUtil jwtUtil;
    private final AccountService accountService;

    public JWTCheckFilter(AuthenticationManager authenticationManager, AccountService accountService, JWTUtil jwtUtil) {
        super(authenticationManager);
        this.accountService = accountService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(JWTUtil.AUTHENTICATION_HEADER);
        if(token == null || !token.startsWith(JWTUtil.BEARER)) {
            chain.doFilter(request,response);
            return;
        }
        VerifyResult result = jwtUtil.verify(token.substring(JWTUtil.BEARER.length()));
        if(result.isResult()) {
            Account account = accountService.getAccount(result.getUsername());
            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(account.generateUserDetails(), null, account.roles())
            );
        }

        super.doFilterInternal(request, response, chain);
    }
}
