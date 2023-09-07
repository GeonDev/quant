package com.quant.core.repository;

import com.quant.core.entity.CorpInfo;
import com.quant.core.enums.CorpState;

import com.quant.core.mapping.CorpCodeMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface CorpInfoRepository extends JpaRepository<CorpInfo, String> {

    Long countByState(CorpState state);

    List<CorpCodeMapper> findByState(CorpState state);

    List<CorpInfo> findByCheckDtBefore(LocalDate date);

    Page<CorpCodeMapper> findByState(Pageable pageable, CorpState state);

}
