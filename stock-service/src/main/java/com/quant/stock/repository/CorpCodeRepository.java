package com.quant.stock.repository;

import com.quant.stock.entity.CorpInfo;
import com.quant.stock.model.enums.CorpState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CorpCodeRepository extends JpaRepository<CorpInfo, String> {

    List<CorpInfo> findByState(CorpState state);

    List<CorpInfo> findByCheckDtBefore(LocalDate date);

}
