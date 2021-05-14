package com.bnpinnovation.turret;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.domain.AccountRole;
import com.bnpinnovation.turret.dto.AccountForm;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.Assertions;

import java.util.Arrays;
import java.util.Optional;

public class AccountTestHelper {
    private final AccountService accountService;
    private final RoleService roleService;

    public AccountTestHelper(AccountService accountService, RoleService roleService) {
        this.accountService = accountService;
        this.roleService = roleService;
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
        Assertions.assertEquals(username, account.getUsername());
        Assertions.assertEquals(username+"p", account.getPassword());
        Assertions.assertEquals(username+"d", account.getName());
        Assertions.assertTrue(account.roles().contains(roleName));
    }
}
