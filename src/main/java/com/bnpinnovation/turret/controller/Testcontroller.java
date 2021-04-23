package com.bnpinnovation.turret.controller;

import com.bnpinnovation.turret.dto.SecurityMessage;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Testcontroller {

    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    @GetMapping("/user")
    public SecurityMessage user() {
        return SecurityMessage.builder()
                .message("user page")
                .auth(SecurityContextHolder.getContext().getAuthentication())
                .build();
    }

    @Secured({"ROLE_ADMIN"})
    @GetMapping("/admin")
    public SecurityMessage admin() {
        return SecurityMessage.builder()
                .message("admin page")
                .auth(SecurityContextHolder.getContext().getAuthentication())
                .build();
    }
}
