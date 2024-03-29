package com.quant.core.repository;

import com.quant.core.entity.CorpInfo;
import com.quant.core.enums.CorpState;

import com.quant.core.repository.mapping.CorpCodeMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CorpInfoRepository extends JpaRepository<CorpInfo, String> {

    Long countByState(CorpState state);

    List<CorpCodeMapper> findByState(CorpState state);

    List<CorpInfo> findByCheckDtBefore(LocalDate date);

    Page<CorpCodeMapper> findByStateAndCorpTypeIsNull(Pageable pageable, CorpState state);

    CorpInfo findTopByCorpCode(String code);

}
