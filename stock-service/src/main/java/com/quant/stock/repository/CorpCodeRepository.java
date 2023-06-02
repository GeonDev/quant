package com.quant.stock.repository;

import com.quant.stock.entity.CorpCode;
import com.quant.stock.model.enums.CorpState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CorpCodeRepository extends JpaRepository<CorpCode, String> {

    Optional<CorpCode> findByIdAndState(String id, CorpState state);

    List<CorpCode> findByCorpState(String corpState);

    List<CorpCode> findByCheckDtBefore(LocalDate date);

}
