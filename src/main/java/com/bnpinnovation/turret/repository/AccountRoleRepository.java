package com.bnpinnovation.turret.repository;

import com.bnpinnovation.turret.domain.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
    Optional<AccountRole> findAllByRoleName(String role);

    boolean existsByRoleName(String role);
}
