package com.bnpinnovation.turret.controller;

import com.bnpinnovation.turret.dto.ThirdForm;
import com.bnpinnovation.turret.service.ThirdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/third")
public class ThirdController {
    @Autowired
    private ThirdService thirdService;

    @PostMapping
    @RequestMapping("")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ThirdForm.ThirdDetails newThird(@RequestBody ThirdForm.New requestForm) {
        return thirdService.newThird(requestForm);
    }

    @GetMapping
    @RequestMapping("/all")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public List<ThirdForm.ThirdDetails> thirds() {
        return thirdService.allThirds();
    }

    @PostMapping
    @RequestMapping("/{id}/refresh")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public ThirdForm.ThirdDetails refreshThird(@PathVariable("id") Long thirdId) {
        return thirdService.refreshThird(thirdId);
    }

    @PostMapping
    @RequestMapping("/{id}/disable")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public void disableThird(@PathVariable("id") Long thirdId) {
        thirdService.disableThird(thirdId);
    }

    @PostMapping
    @RequestMapping("/{id}/enable")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN')")
    public void enableThird(@PathVariable("id") Long thirdId) {
        thirdService.enableThird(thirdId);
    }
}