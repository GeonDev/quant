package com.quant.core.repository;

import com.quant.core.entity.Trade;
import com.quant.core.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, String> {

    Optional<Trade> findByUserKeyAndStockCode(String key, String code);

}
