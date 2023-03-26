package com.stock.quant.service.repository;

import com.stock.quant.service.entity.StockPrice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockPriceRepository extends JpaRepository<StockPrice, String> {
}
