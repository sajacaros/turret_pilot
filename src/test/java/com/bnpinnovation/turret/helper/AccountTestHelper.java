package com.bnpinnovation.turret.helper;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.domain.AccountRole;
import com.bnpinnovation.turret.dto.AccountForm;
import com.bnpinnovation.turret.security.JWTUtil;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AccountTestHelper {
    private final AccountService accountService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final int port;
    private RestTemplate restTemplate = new RestTemplate();

    public AccountTestHelper(AccountService accountService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this(accountService,roleService,passwordEncoder,0);
    }

    public AccountTestHelper(AccountService accountService, RoleService roleService, PasswordEncoder passwordEncoder, int port) {
        this.accountService = accountService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
        this.port = port;
    }

    public AccountRole createRole(String roleName) {
        Long roleId = roleService.newRole(roleName);
        AccountRole role = roleService.getAccountRole(roleName);
        Assertions.assertEquals(roleId, role.id());
        return role;
    }

    public Account createUserAndRole(String username, String roleName) {
        createRole(roleName);
        return createUser(username, roleName);
    }

    public Account createUser(String username, String roleName) {
        AccountForm.NewAccount accountDto = new AccountForm.NewAccount();
        accountDto.setUsername(username);
        accountDto.setPassword(username+"p");
        accountDto.setName(username+"d");
        accountDto.setRoles(Arrays.asList(roleName));

        Long id = accountService.newAccount(accountDto);

        Optional<Account> accountOptional = accountService.findAccount(id);
        Assertions.assertTrue(accountOptional.isPresent());
        assertAccount(accountOptional.get(), username, roleName);

        return accountOptional.get();
    }

    public void assertAccount(Account account, String username, String roleName) {
        Assertions.assertNotNull(account.id());
        Assertions.assertNotNull(account.getCreatedDate());
        Assertions.assertNotNull(account.getUpdatedDate());
        Assertions.assertTrue(account.isEnabled());
        Assertions.assertTrue(account.isAccountNonExpired());
        Assertions.assertTrue(account.isAccountNonLocked());
        Assertions.assertTrue(account.isCredentialsNonExpired());
        Assertions.assertEquals(username, account.username());
        Assertions.assertTrue(passwordEncoder.matches(username+"p", account.password() ));
        Assertions.assertEquals(username+"d", account.name());
    }

    private URI uri(String path) throws URISyntaxException {
        return new URI(String.format("http://localhost:%d%s", port, path));
    }

    public Long newAccount(String token, String name, String password, String roleName) throws URISyntaxException {
        return newAccount(token,name,password,roleName,null);
    }
    public Long newAccount(String token, String name, String password, String roleName,  ResponseErrorHandler errorHandler) throws URISyntaxException {
        AccountForm.NewAccount newAccount = AccountForm.NewAccount.builder()
                .username(name)
                .password(password)
                .name(name+"d")
                .roles(Arrays.asList(roleName))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(JWTUtil.AUTHENTICATION_HEADER, JWTUtil.BEARER+token);
        HttpEntity<AccountForm.NewAccount> body = new HttpEntity<>(newAccount,headers);
        if(errorHandler != null) {
            restTemplate.setErrorHandler(errorHandler);
        }
        ResponseEntity<Long> response = restTemplate.exchange(uri("/scv/account"), HttpMethod.POST, body, Long.class);
        assertEquals(HttpServletResponse.SC_OK, response.getStatusCodeValue());
        return response.getBody().longValue();
    }
}
