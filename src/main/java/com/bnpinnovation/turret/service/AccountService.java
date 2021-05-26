package com.bnpinnovation.turret.service;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.domain.AccountRole;
import com.bnpinnovation.turret.dto.AccountForm;
import com.bnpinnovation.turret.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface AccountService extends UserDetailsService {
    Long newAccount(AccountForm.NewAccount account);

    Optional<Account> findAccount(Long id);

    List<Account> findAll();

    boolean existUserName(String username);

    Account getAccount(String username);

    Collection<AccountRole> retrieveRole(String username);

    void removeAll();

    @Service
    @Slf4j
    @Transactional
    class Default implements AccountService {
        @Autowired
        private AccountRepository accountRepository;
        @Autowired
        private RoleService roleService;
        @Autowired
        private PasswordEncoder passwordEncoder;

        @Override
        public Long newAccount(AccountForm.NewAccount accountDto) {
            List<AccountRole> roleList;
            if(accountDto.getRoles() == null || accountDto.getRoles().isEmpty()) {
                roleList = Collections.emptyList();
            } else {
                roleList = accountDto.getRoles().stream()
                        .map(r->getAccountRole(r))
                        .collect(Collectors.toList());
            }


            Account account = Account.builder()
                    .username(accountDto.getUsername())
                    .password(passwordEncoder.encode(accountDto.getPassword()))
                    .name(accountDto.getName())
                    .enabled(true)
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();
            account.addRoles(roleList);

            Account savedAccount = accountRepository.save(account);
            return savedAccount.id();
        }

        @Override
        public Optional<Account> findAccount(Long id) {
            return accountRepository.findById(id);
        }

        @Override
        public List<Account> findAll() {
            return accountRepository.findAll();
        }

        @Override
        public boolean existUserName(String username) {
            return accountRepository.findByUsername(username).isPresent();
        }

        @Override
        public Account getAccount(String username) {
            return accountRepository.findByUsername(username)
                    .orElseThrow(()->new UsernameNotFoundException(username+" not exist"));
        }

        @Override
        public Collection<AccountRole> retrieveRole(String username) {
            return getAccount(username).roles();
        }

        @Override
        public void removeAll() {
            for( Account account: accountRepository.findAll()) {
                account.cleanRole();
                accountRepository.delete(account);
            }
        }

        @Override
        public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
            return accountRepository.findByUsername(username)
                    .orElseThrow(()->new UsernameNotFoundException(username+" is not exist"))
                    .generateUserDetails();
        }

        private AccountRole getAccountRole(String roleName) {
            return roleService.getAccountRole(roleName);
        }
    }
}
