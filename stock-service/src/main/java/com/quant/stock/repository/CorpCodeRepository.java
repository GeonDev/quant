package com.quant.stock.repository;

import com.quant.stock.entity.CorpCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorpCodeRepository extends JpaRepository<CorpCode, String> {
}
