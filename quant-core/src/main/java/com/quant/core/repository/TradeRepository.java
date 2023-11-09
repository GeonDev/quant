package com.quant.core.repository;

import com.quant.core.entity.Trade;
import com.quant.core.entity.UserInfo;
import com.quant.core.enums.TradingType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TradeRepository extends JpaRepository<Trade, String> {

    List<Trade> findByUserInfoAndStockCode(UserInfo userInfo, String code);

    Integer countByUserInfoAndStockCodeAndTradeType(UserInfo userInfo, String code, TradingType tradingType);


}
