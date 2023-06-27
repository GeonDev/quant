package com.quant.core.repository;

import com.quant.core.entity.StockAverage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockAverageRepository extends JpaRepository<StockAverage, String> {
}
