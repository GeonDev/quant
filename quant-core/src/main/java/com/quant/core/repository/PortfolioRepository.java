package com.quant.core.repository;

import com.quant.core.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, String> {

  Optional<Portfolio> findByPortId(String id);
}
