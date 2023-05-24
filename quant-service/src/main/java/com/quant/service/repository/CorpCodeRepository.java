package com.stock.quant.service.repository;

import com.stock.quant.service.entity.CorpCode;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CorpCodeRepository extends JpaRepository<CorpCode, String> {
}
