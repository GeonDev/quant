package com.quant.core.repository;

import com.quant.core.entity.StockAverage;
import com.quant.core.enums.PriceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockAverageRepository extends JpaRepository<StockAverage, String> {


    List<StockAverage> findByStockCodeAndTarDt(String stockCode, LocalDate date);

    Optional<StockAverage> findByStockCodeAndTarDtAndPriceType(String stockCode, LocalDate date, PriceType priceType);

}
