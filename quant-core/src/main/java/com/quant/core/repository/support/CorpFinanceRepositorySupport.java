package com.quant.core.repository.support;

import com.quant.core.dto.StockDto;
import com.quant.core.entity.CorpFinance;
import com.quant.core.enums.AmtRange;
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

import static com.quant.core.entity.QCorpFinance.corpFinance;
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
                .constructor(StockDto.class, corpInfo.corpName, corpFinance.stockCode, stockPrice.endPrice))
                .from(corpFinance)
                .join(corpInfo).on(corpFinance.corpCode.eq(corpInfo.corpCode)).leftJoin(stockPrice).on(stockPrice.stockCode.eq(corpFinance.stockCode))
                .where(stockPrice.basDt.eq(date), rangeSet(date, range), upperZero(indicator) , corpInfo.momentum.goe(momentum) ,marketType(market))
                .limit(limit)
                .orderBy(setOrderSpecifier(indicator))
                .fetch();
    }


    // 동적으로 오더 순서 생성
    private OrderSpecifier[] createOrderSpecifier(List<String> keyList) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        for (String key : keyList) {

            if (key.equals("PSR")) {
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, corpFinance.PSR));
            } else if (key.equals("POR")) {
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, corpFinance.POR));
            } else if (key.equals("PER")) {
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, corpFinance.PER));
            }
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }

    //지표에 따른 오더 결정
    private OrderSpecifier setOrderSpecifier(String key) {
        switch (key) {
            case "PSR":
                return new OrderSpecifier(Order.ASC, corpFinance.PSR);
            case "PBR":
                return new OrderSpecifier(Order.ASC, corpFinance.PBR);
            case "PER":
                return new OrderSpecifier(Order.ASC, corpFinance.PER);
            case "POR":
                return new OrderSpecifier(Order.ASC, corpFinance.POR);
            case "YOY":
                return new OrderSpecifier(Order.DESC, corpFinance.YOY);
            case "QOQ":
                return new OrderSpecifier(Order.DESC, corpFinance.QOQ);
            case "OPGE":
                return new OrderSpecifier(Order.DESC, corpFinance.OPGE);
            case "PGE":
                return new OrderSpecifier(Order.DESC, corpFinance.PGE);
            default:
                return new OrderSpecifier(Order.DESC, corpFinance.revenue);
        }
    }

    //정렬 대상이 최소 0 이상인 경우만 체크
    private BooleanExpression upperZero(String key) {
        switch (key) {
            case "PSR":
                return corpFinance.PSR.gt(0);
            case "PBR":
                return corpFinance.PBR.gt(0);
            case "PER":
                return corpFinance.PER.gt(0);
            case "POR":
                return corpFinance.POR.gt(0);
            case "YOY":
                return corpFinance.YOY.gt(0);
            case "QOQ":
                return corpFinance.QOQ.gt(0);
            case "OPGE":
                return corpFinance.OPGE.gt(0);
            case "PGE":
                return corpFinance.PGE.gt(0);
            default:
                return corpFinance.revenue.gt(0);
        }
    }

    private BooleanExpression rangeSet(LocalDate date, AmtRange range) {
        if (range.equals(AmtRange.LOWER20)) {

            List<Integer> price = queryFactory.select(stockPrice.endPrice).from(stockPrice).where(stockPrice.basDt.eq(date)).orderBy(stockPrice.marketTotalAmt.asc()).fetch();

            int per20 = (int) Math.floor(price.size() * 0.2f);

            return stockPrice.endPrice.loe(price.get(per20));

        } else if (range.equals(AmtRange.UPPER200)) {

            Integer price = queryFactory.select(stockPrice.endPrice).from(stockPrice).where(stockPrice.basDt.eq(date)).orderBy(stockPrice.marketTotalAmt.desc()).fetch().get(199);

            return stockPrice.endPrice.goe(price);
        }
        return null;
    }

    private BooleanExpression marketType(String market) {
        if (market.equalsIgnoreCase("KOSPI") ) {
            return stockPrice.marketCode.eq("KOSPI");

        } else if (market.equalsIgnoreCase("KOSDAQ")) {
            return stockPrice.marketCode.eq("KOSDAQ");
        }
        return null;
    }


}




