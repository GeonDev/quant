package com.quant.core.repository;

import com.quant.core.entity.CorpFinance;
import com.quant.core.repository.mapping.FinanceMapper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CorpFinanceRepository extends JpaRepository<CorpFinance, Long> {

    Optional<CorpFinance> findByCorpCodeAndReprtCodeAndYearCode(String corpCode, String reprtCode, String year);

    FinanceMapper findTopByCorpCodeAndOperatingProfitIsNotNull(String corpCode);

}
