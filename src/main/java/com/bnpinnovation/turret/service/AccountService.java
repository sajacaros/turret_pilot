package com.bnpinnovation.turret.service;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.domain.AccountRole;
import com.bnpinnovation.turret.dto.AccountForm;
import com.bnpinnovation.turret.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public interface AccountService {
    Long newAccount(AccountForm.NewAccount account);

    Optional<Account> findAccount(Long id);

    @Service
    @Slf4j
    @Transactional
    class Default implements AccountService, UserDetailsService {
        @Autowired
        private AccountRepository accountRepository;
        @Autowired
        private RoleService roleService;

        @Override
        public Long newAccount(AccountForm.NewAccount accountDto) {
            List<AccountRole> roleList = accountDto.getRoles().stream()
                    .map(r->getAccountRole(r))
                    .collect(Collectors.toList());
            IncorrectResultSizeDataAccessException a;
            DuplicateKeyException d;

            Account account = Account.builder()
                    .username(accountDto.getUsername())
                    .password(accountDto.getPassword())
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
