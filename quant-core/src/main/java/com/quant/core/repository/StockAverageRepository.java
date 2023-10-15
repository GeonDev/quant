package com.quant.core.repository;

import com.quant.core.entity.StockAverage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StockAverageRepository extends JpaRepository<StockAverage, String> {

    List<StockAverage> findByStockCodeAndTarDt(String stockCode, LocalDate date);

}
