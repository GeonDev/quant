package com.quant.core.repository;

import com.quant.core.entity.CorpFinance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CorpFinanceRepository extends JpaRepository<CorpFinance, Long> {

    CorpFinance findByCorpCodeAndRceptNoAndYears(String corpCode, String rceptNo, String year);

    List<CorpFinance> findByRceptNoAndYears(String rceptNo, String year);

}
