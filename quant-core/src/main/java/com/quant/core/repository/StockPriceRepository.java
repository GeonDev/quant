package com.quant.core.repository;

import com.quant.core.entity.StockPrice;
import com.quant.core.repository.mapping.PriceMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByStockCodeAndBasDtBefore(String code, LocalDate date, Pageable page);

    PriceMapper findTopByStockCodeAndBasDtBetweenOrderByBasDtDesc(String code, LocalDate sdate, LocalDate edate);

    Integer countByStockCodeAndBasDt(String code, LocalDate date);

    Integer countByStockCode(String code);


}
