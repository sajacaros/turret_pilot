package com.bnpinnovation.turret.domain;

import lombok.*;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Entity
@NoArgsConstructor
@ToString(exclude = {"roles"})
public class Account extends TimeEntity {
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

    @ManyToMany(fetch=FetchType.EAGER)
    @JoinTable(name = "USER_ROLE", joinColumns = @JoinColumn(name = "USER_ID"), inverseJoinColumns = @JoinColumn(name = "ROLE_ID"))
    @Setter(AccessLevel.NONE)
    private Set<AccountRole> roles = new HashSet<>();

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

    public UserDetails generateUserDetails() {
        return new User(username,password,enabled,accountNonExpired,credentialsNonExpired,accountNonLocked,roles());
    }

    public Set<AccountRole> roles() {
        if(roles.isEmpty()) {
            return Collections.emptySet();
        } else {
            return Collections.unmodifiableSet(roles);
        }
    }

    public void addRole(AccountRole role) {
        roles.add(role);
    }

    public void addRoles(Iterable<AccountRole> roles) {
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

    public String username() {
        return this.username;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public String password() {
        return this.password;
    }

    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }
}
