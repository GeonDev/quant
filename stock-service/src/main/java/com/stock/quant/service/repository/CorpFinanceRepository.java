package com.stock.quant.service.repository;

import com.stock.quant.service.entity.CorpFinance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorpFinanceRepository extends JpaRepository<CorpFinance, String> {
}
