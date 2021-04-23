package com.bnpinnovation.turret.controller;

import com.bnpinnovation.turret.dto.AccountForm;
import com.bnpinnovation.turret.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping
    public Long newAccount(AccountForm.NewAccount account) {
        return accountService.newAccount(account);
    }
}
