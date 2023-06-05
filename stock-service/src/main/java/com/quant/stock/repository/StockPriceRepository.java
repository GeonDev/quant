package com.quant.stock.repository;

import com.quant.stock.entity.StockPrice;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockPriceRepository extends JpaRepository<StockPrice, Long> {
    List<StockPrice> findByStockCodeAndBasDtBefore(String code, LocalDate date, Pageable page);
}
