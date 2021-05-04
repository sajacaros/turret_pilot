package com.bnpinnovation.turret.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Entity
public class Account {
    @Id
    @Column(name="ACCOUNT_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;
    private String password;
    private String name;
    private boolean enabled;  // 계정 활성화 여부
    private boolean accountNonExpired; // 계정 만료 여부
    private boolean credentialsNonExpired; // 패스워드 만료 여부
    private boolean accountNonLocked; // 계정 잠금 여부

    @Builder
    public Account(
            String username,
            String password,
            String name,
            boolean enabled,
            boolean accountNonExpired,
            boolean credentialsNonExpired,
            boolean accountNonLocked
    ) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.enabled = enabled;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.accountNonLocked = accountNonLocked;
    }

    @ManyToMany
    @JoinTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @Setter(AccessLevel.NONE)
    private Set<AccountRole> roles = new HashSet<>();

    @CreatedDate
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime created;
    @LastModifiedDate
    @Column(columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime updated;

    public UserDetails generateUserDetails() {
        return new User(username,password,enabled,accountNonExpired,credentialsNonExpired,accountNonLocked,roles());
    }

    public Set<AccountRole> roles() {
        return Collections.unmodifiableSet(roles);
    }

    public void addRole(AccountRole role) {
        roles.add(role);
    }

    public void addRoles(Set<AccountRole> roles) {
        for(AccountRole role : roles) {
            addRole(role);
        }
    }

    public void remove(AccountRole role) {
        roles.remove(role);
    }

    public void removeAll() {
        roles.clear();
    }

    public String name() {
        return this.name;
    }

    public Long id() {
        return id;
    }
}
