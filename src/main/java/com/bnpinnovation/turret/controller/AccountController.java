package com.bnpinnovation.turret.controller;

import com.bnpinnovation.turret.dto.AccountForm;
import com.bnpinnovation.turret.service.AccountService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    @RequestMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public Long newAccount(@RequestBody AccountForm.NewAccount account) {
        return accountService.newAccount(account);
    }
}
