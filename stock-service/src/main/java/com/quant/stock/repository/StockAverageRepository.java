package com.quant.stock.repository;

import com.quant.stock.entity.StockAverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockAverageRepository extends JpaRepository<StockAverage, String> {
}
