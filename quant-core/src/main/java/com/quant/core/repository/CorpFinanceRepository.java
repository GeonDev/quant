package com.quant.core.repository;

import com.quant.core.entity.CorpFinance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorpFinanceRepository extends JpaRepository<CorpFinance, Long> {

    CorpFinance findByCorpCodeAndRceptNoAndYearCode(String corpCode, String rceptNo, String year);

}
