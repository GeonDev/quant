package com.quant.core.repository.support;

import com.quant.core.dto.StockDto;
import com.quant.core.entity.CorpFinance;
import com.quant.core.enums.AmtRange;
import com.quant.core.enums.PriceType;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


import static com.quant.core.entity.QStockAverage.stockAverage;
import static com.quant.core.entity.subSelect.QsubFinance.subFinance;
import static com.quant.core.entity.QCorpInfo.corpInfo;
import static com.quant.core.entity.QStockPrice.stockPrice;

@Repository
public class CorpFinanceRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public CorpFinanceRepositorySupport(JPAQueryFactory queryFactory) {
        super(CorpFinance.class);
        this.queryFactory = queryFactory;
    }

    //인디케이터의 조건에 맞는 주식 리스트를 추출
    public List<StockDto> findByStockOrderSet(LocalDate date, String market, String indicator, AmtRange range, Integer limit, Integer momentum) {
        return queryFactory.select(Projections
                        .constructor(StockDto.class, corpInfo.corpName, corpInfo.stockCode, stockPrice.endPrice))
                .from(corpInfo)
                .join(subFinance).on(corpInfo.corpCode.eq(subFinance.corpCode))
                .leftJoin(stockPrice).on(stockPrice.stockCode.eq(corpInfo.stockCode))
                .leftJoin(stockAverage).on(stockAverage.stockCode.eq(stockPrice.stockCode).and(stockAverage.tarDt.eq(stockPrice.basDt)).and(stockAverage.priceType.eq(PriceType.DAY200)) )
                .where(
                        stockPrice.basDt.eq(date),
                        stockPrice.endPrice.goe(stockAverage.price),
                        rangeSet(date, range),
                        upperZero(),
                        corpInfo.momentum.goe(momentum),
                        marketType(market),
                        corpInfo.corpType.isNull()
                )
                .orderBy(setOrderSpecifier(indicator), corpInfo.momentum.desc())
                .limit(limit)
                .fetch();
    }


    //지표에 따른 오더 결정
    private OrderSpecifier setOrderSpecifier(String key) {
        return switch (key) {
            case "PSR" -> new OrderSpecifier(Order.ASC, subFinance.PSR);
            case "PBR" -> new OrderSpecifier(Order.ASC, subFinance.PBR);
            case "PER" -> new OrderSpecifier(Order.ASC, subFinance.PER);
            case "POR" -> new OrderSpecifier(Order.ASC, subFinance.POR);
            case "YOY" -> new OrderSpecifier(Order.DESC, subFinance.YOY);
            case "QOQ" -> new OrderSpecifier(Order.DESC, subFinance.QOQ);
            case "OPGE" -> new OrderSpecifier(Order.DESC, subFinance.OPGE);
            case "PGE" -> new OrderSpecifier(Order.DESC, subFinance.PGE);
            default -> new OrderSpecifier(Order.DESC, subFinance.revenue);
        };
    }

    //정렬 대상이 최소 0 이상인 경우만 체크
    private BooleanExpression upperZero() {
        return subFinance.PSR.gt(0)
                .and(subFinance.PBR.gt(0) )
                .and(subFinance.PER.gt(0) )
                .and(subFinance.POR.gt(0))
                .and(subFinance.YOY.gt(0))
                .and(subFinance.QOQ.gt(0))
                .and(subFinance.OPGE.gt(0))
                .and(subFinance.revenue.gt(0));
    }

    private BooleanExpression rangeSet(LocalDate date, AmtRange range) {
        if (range.equals(AmtRange.LOWER20)) {
            List<Integer> price = queryFactory.select(stockPrice.endPrice).from(stockPrice).where(stockPrice.basDt.eq(date)).orderBy(stockPrice.marketTotalAmt.asc()).fetch();
            if (!price.isEmpty()) {
                int per20 = (int) Math.floor(price.size() * 0.2f);
                return stockPrice.endPrice.loe(price.get(per20));
            }
        } else if (range.equals(AmtRange.UPPER200)) {
            Integer price = queryFactory.select(stockPrice.endPrice).from(stockPrice).where(stockPrice.basDt.eq(date)).orderBy(stockPrice.marketTotalAmt.desc()).fetch().get(199);
            if (price != null) {
                return stockPrice.endPrice.goe(price);
            }
        }
        return null;
    }

    private BooleanExpression marketType(String market) {
        if (market.equalsIgnoreCase("KOSPI")) {
            return stockPrice.marketCode.eq("KOSPI");

        } else if (market.equalsIgnoreCase("KOSDAQ")) {
            return stockPrice.marketCode.eq("KOSDAQ");
        }
        return null;
    }

}




