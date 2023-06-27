package com.quant.core.repository;

import com.quant.core.entity.CorpInfo;
import com.quant.core.enums.CorpState;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CorpInfoRepository extends JpaRepository<CorpInfo, String> {

    List<CorpInfo> findByState(CorpState state);

    List<CorpInfo> findByCheckDtBefore(LocalDate date);

}
