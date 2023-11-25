package com.quant.core.repository;

import com.quant.core.entity.CorpFinance;
import com.quant.core.repository.mapping.FinanceMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorpFinanceRepository extends JpaRepository<CorpFinance, Long> {

    CorpFinance findByCorpCodeAndReprtCodeAndYearCode(String corpCode, String reprtCode, String year);

    FinanceMapper findTopByCorpCodeAndOperatingProfitIsNotNull(String corpCode);

}
