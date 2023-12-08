package com.quant.core.repository.support;

import com.quant.core.dto.HoldStockDto;
import com.quant.core.entity.CorpFinance;
import com.quant.core.entity.Trade;
import com.quant.core.entity.UserInfo;
import com.quant.core.enums.TradingType;
import com.quant.core.exception.InvalidRequestException;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
    public Integer countByTradeStock(String portKey, String code){
        List<Integer> buyStockCount = queryFactory
                .select(trade.stockCount.sum())
                .from(trade)
                .where(trade.portfolio().portId.eq(portKey), trade.tradeType.eq(TradingType.BUY), trade.stockCode.eq(code))
                .fetch();

        List<Integer> sellStockCount = queryFactory
                .select(trade.stockCount.sum())
                .from(trade)
                .where(trade.portfolio().portId.eq(portKey), trade.tradeType.eq(TradingType.SELL),trade.stockCode.eq(code))
                .fetch();

        if(sellStockCount.get(0) > buyStockCount.get(0)){
            throw new InvalidRequestException("보유 주식 수량 오류");
        }


        return buyStockCount.get(0) - sellStockCount.get(0);
    }


    //현재 보유중인 주식 리스트
    public List<HoldStockDto>  findByTradeStock(String portKey){

        List<HoldStockDto> buyStockList = queryFactory
                .select( Projections.constructor(HoldStockDto.class, trade.stockCode , trade.stockCount.sum()))
                .from(trade)
                .where(trade.portfolio().portId.eq(portKey), trade.tradeType.eq(TradingType.BUY))
                .groupBy(trade.stockCode)
                .fetch();

        List<HoldStockDto> sellStockList = queryFactory
                .select( Projections.constructor(HoldStockDto.class, trade.stockCode , trade.stockCount.sum()))
                .from(trade)
                .where(trade.portfolio().portId.eq(portKey), trade.tradeType.eq(TradingType.SELL))
                .fetch();

        for(HoldStockDto buyStock : buyStockList ){
            buyStock.setCount(buyStock.getCount() - getTradeListCount(sellStockList, buyStock.getStockCode()) );
        }

        return buyStockList;
    }

    private Integer getTradeListCount(List<HoldStockDto> itemList, String stockCode){

        for(HoldStockDto item : itemList ){
            if(item.getStockCode().equals(stockCode)){
                return  item.getCount();
            }
        }


        return 0;
    }


}
