package com.bnpinnovation.turret.helper;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.domain.AccountRole;
import com.bnpinnovation.turret.dto.AccountForm;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.Assertions;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Optional;

public class AccountTestHelper {
    private final AccountService accountService;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public AccountTestHelper(AccountService accountService, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.accountService = accountService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
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
//        Assertions.assertTrue(
//                account.roles().stream()
//                        .map(r->r.getAuthority())
//                        .filter(rn->roleName.equalsIgnoreCase(rn))
//                        .findAny().isPresent()
//        );
    }
}
