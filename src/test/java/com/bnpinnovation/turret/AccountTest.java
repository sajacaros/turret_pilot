package com.bnpinnovation.turret;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.domain.AccountRole;
import com.bnpinnovation.turret.helper.AccountTestHelper;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccountTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private AccountTestHelper accountHelper;

    private String username = "hello";
    private String roleName = "master";

    @BeforeEach
    void before() {
        if( this.accountHelper == null) {
            this.accountHelper = new AccountTestHelper(accountService, roleService, passwordEncoder);
        }
    }

    @AfterEach
    void after() {
        accountService.removeAll();
    }

    @DisplayName("1. 사용자를 생성한다.")
    @Test
    void test_user_create() {
        Account account = accountHelper.createUserAndRole(username,roleName);
        Optional<Account> searchedAccountOptional = accountService.findAccount(account.id());
        assertTrue(searchedAccountOptional.isPresent());
        assertEquals(username, searchedAccountOptional.get().username());
        Collection<AccountRole> roles = accountService.retrieveRole(username);
        assertTrue(roles.stream()
                .map(r->r.getAuthority())
                .filter(rn->roleName.equalsIgnoreCase(rn))
                .findAny().isPresent());
    }

    @DisplayName("2. 사용자를 중복으로 생성한다.")
    @Test
    void test_user_duplication_create() {
        accountHelper.createUserAndRole(username,roleName);
        assertThrows(DataIntegrityViolationException.class, ()->accountHelper.createUserAndRole(username,roleName+'1'));
    }
}
