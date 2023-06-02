package com.quant.stock.repository;

import com.quant.stock.entity.CorpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CorpCodeRepository extends JpaRepository<CorpCode, String> {

    Optional<CorpCode> findByIdAAndCorpState(String id, String corpState);

    List<CorpCode> findByCorpState(String corpState);

}
