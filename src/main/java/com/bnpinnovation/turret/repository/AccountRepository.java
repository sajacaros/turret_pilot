package com.bnpinnovation.turret.repository;

import com.bnpinnovation.turret.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByUsername(String username);
}
