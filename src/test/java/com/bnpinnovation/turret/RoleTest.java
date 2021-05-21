package com.bnpinnovation.turret;

import com.bnpinnovation.turret.domain.AccountRole;
import com.bnpinnovation.turret.helper.AccountTestHelper;
import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class RoleTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private AccountTestHelper accountHelper;

    String roleName = "master";

    @BeforeEach
    void before() {
        if( this.accountHelper == null) {
            this.accountHelper = new AccountTestHelper(accountService, roleService, passwordEncoder);
        }
    }

    @DisplayName("1. 존재하지 않는 role을 조회한다.")
    @Test
    void test_role_not_exist() {
        assertThrows( EntityNotFoundException.class, ()->roleService.getAccountRole(roleName));
    }

    @DisplayName("2. role을 생성한다.")
    @Test
    void test_role_create() {
        AccountRole role = accountHelper.createRole(roleName);
        AccountRole searchedRole = roleService.getAccountRole(roleName);
        assertEquals(role, searchedRole);
    }

    @DisplayName("3. role을 중복으로 생성한다.")
    @Test
    void test_role_duplicate_create() {
        accountHelper.createRole(roleName);
    }

}
