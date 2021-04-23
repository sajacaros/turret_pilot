package com.bnpinnovation.turret.service;

import com.bnpinnovation.turret.domain.Account;
import com.bnpinnovation.turret.domain.AccountRole;
import com.bnpinnovation.turret.dto.AccountForm;
import com.bnpinnovation.turret.repository.AccountRepository;
import com.bnpinnovation.turret.repository.AccountRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.Optional;

public interface AccountService {
    Long newAccount(AccountForm.NewAccount account);

    @Service
    @Slf4j
    class Default implements AccountService {
        @Autowired
        private AccountRepository accountRepository;
        @Autowired
        private AccountRoleRepository roleRepository;

        @Override
        public Long newAccount(AccountForm.NewAccount accountDto) {
            Optional<Account> accountOptional = accountRepository.findByUsername(accountDto.getUsername());
            if(accountOptional.isPresent()) {
                throw new EntityExistsException(accountOptional.get().name()+"이 존재함");
            }

            AccountRole role = roleRepository.findAllByRoleName(accountDto.getRole())
                    .orElseThrow(() -> new EntityNotFoundException(accountDto.getRole()+"이 존재하지 않음"));

            Account account = Account.builder()
                    .username(accountDto.getUsername())
                    .password(accountDto.getPassword())
                    .name(accountDto.getName())
                    .accountNonExpired(true)
                    .accountNonLocked(true)
                    .credentialsNonExpired(true)
                    .build();
            account.addRole(role);

            Account savedAccount = accountRepository.save(account);
            return savedAccount.id();
        }
    }
}
