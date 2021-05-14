package com.bnpinnovation.turret.domain;

import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Entity
@Table(name = "account_role")
@NoArgsConstructor
public class AccountRole implements GrantedAuthority {
    @Id
    @Column(name = "ROLE_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "ROLE_NAME", nullable = false)
    private String roleName;

    public AccountRole(String name) {
        this.roleName = name;
    }

    @Override
    public String getAuthority() {
        return roleName;
    }

    public Long id() {
        return this.id;
    }
}