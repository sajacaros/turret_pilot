package com.bnpinnovation.turret.repository;

import com.bnpinnovation.turret.domain.Third;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ThirdRepository extends JpaRepository<Third, Long> {
    Optional<Third> findBySymbol(String symbol);

    boolean existsBySymbol(String symbol);
}
