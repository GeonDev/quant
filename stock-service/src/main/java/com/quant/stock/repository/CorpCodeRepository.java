package com.quant.stock.repository;

import com.quant.stock.entity.CorpCode;
import com.quant.stock.model.enums.CorpState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CorpCodeRepository extends JpaRepository<CorpCode, String> {

    List<CorpCode> findByState(CorpState state);

    List<CorpCode> findByCheckDtBefore(LocalDate date);

}
