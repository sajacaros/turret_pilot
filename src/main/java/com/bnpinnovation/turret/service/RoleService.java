package com.bnpinnovation.turret.service;

import com.bnpinnovation.turret.domain.AccountRole;
import com.bnpinnovation.turret.repository.AccountRoleRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

public interface RoleService {
    Long newRole(String roleName);
    AccountRole getAccountRole(String roleName);

    @Service
    @Slf4j
    @Transactional
    class Default implements RoleService {
        @Autowired
        private AccountRoleRepository roleRepository;

        @Override
        public Long newRole(String roleName) {
            if(roleRepository.existsByRoleName(roleName)) {
                return getAccountRole(roleName).id();
            }
            return roleRepository.save(new AccountRole(roleName)).id();
        }

        @Override
        public AccountRole getAccountRole(String roleName) {
            return roleRepository.findAllByRoleName(roleName)
                    .orElseThrow(() -> new EntityNotFoundException(roleName+"이 존재하지 않음"));
        }
    }
}
