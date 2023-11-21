package com.quant.core.repository;

import com.quant.core.entity.StockPrice;
import com.quant.core.repository.mapping.PriceMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByStockCodeAndBasDtBefore(String code, LocalDate date, Pageable page);

    PriceMapper findTopByStockCodeAndBasDtBetweenOrderByBasDtDesc(String code, LocalDate sDate, LocalDate eDate);

    PriceMapper findTopByStockCodeOrderByBasDtDesc(String code);

    Integer countByStockCodeAndBasDt(String code, LocalDate date);

    Integer countByStockCode(String code);


}
