package com.bnpinnovation.turret;

import com.bnpinnovation.turret.service.AccountService;
import com.bnpinnovation.turret.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AccountTest {
    @Autowired
    private AccountService accountService;
    @Autowired
    private RoleService roleService;

    private AccountTestHelper accountHelper;

    @BeforeEach
    void before() {
        this.accountHelper = new AccountTestHelper(accountService,roleService);
    }

    @DisplayName("1. 사용자를 생성한다.")
    @Test
    void test_user_create() {
        accountHelper.createUserAndRole("hello","master")
    }
}
