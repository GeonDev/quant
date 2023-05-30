package com.quant.stock.repository;

import com.quant.stock.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, String> {
}
