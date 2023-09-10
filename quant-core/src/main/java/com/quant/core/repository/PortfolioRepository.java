package com.quant.core.repository;

import com.quant.core.entity.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioRepository extends JpaRepository<Portfolio, String> {


    Portfolio findByPortfolioId(String id);
}
