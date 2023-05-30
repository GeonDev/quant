package com.quant.stock.repository;

import com.quant.stock.entity.CorpFinance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorpFinanceRepository extends JpaRepository<CorpFinance, String> {
}
