package com.quant.core.repository.support;

import com.quant.core.entity.CorpFinance;
import com.quant.core.entity.Trade;
import com.quant.core.entity.UserInfo;
import com.quant.core.enums.TradingType;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.quant.core.entity.QTrade.trade;

@Repository
public class TradeRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public TradeRepositorySupport(JPAQueryFactory queryFactory) {
        super(Trade.class);
        this.queryFactory = queryFactory;
    }

    //현재 보유중인 주식 개수를 반환
    public Integer countByTradeStock(String userKey, String code){
        List<Integer> buyStockCount = queryFactory
                .select(trade.stockCount.sum())
                .from(trade)
                .where(trade.userInfo().userKey.eq(userKey), trade.tradeType.eq(TradingType.BUY), trade.stockCode.eq(code))
                .fetch();

        List<Integer> sellStockCount = queryFactory
                .select(trade.stockCount.sum())
                .from(trade)
                .where(trade.userInfo().userKey.eq(userKey), trade.tradeType.eq(TradingType.SELL),trade.stockCode.eq(code))
                .fetch();

        return buyStockCount.get(0) - sellStockCount.get(0);
    }

}
