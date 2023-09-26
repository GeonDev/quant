package com.quant.core.repository;

import com.quant.core.entity.StockAverage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface StockAverageRepository extends JpaRepository<StockAverage, String> {

    List<StockAverage> findByStockCodeAndTarDt(String stockCode, LocalDate date);

}
