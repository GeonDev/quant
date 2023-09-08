package com.quant.core.repository.support;

import com.quant.core.entity.CorpFinance;
import com.quant.core.mapping.dto.CorpFinanceSimpleDto;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static com.quant.core.entity.QCorpFinance.corpFinance;

@Repository
public class CorpFinanceRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public CorpFinanceRepositorySupport(JPAQueryFactory queryFactory) {
        super(CorpFinance.class);
        this.queryFactory = queryFactory;
    }


    public List<CorpFinanceSimpleDto> findByFinanceSimple(Long id, List<String> orderList){

        List<CorpFinanceSimpleDto> results = queryFactory
                .select(
                        Projections.constructor(CorpFinanceSimpleDto.class,
                                corpFinance.financeId,
                                new CaseBuilder()
                                        .when(corpFinance.corpCode.eq("Q1")).then("1분기")
                                        .when(corpFinance.corpCode.eq("Q2")).then("2분기")
                                        .when(corpFinance.corpCode.eq("Q3")).then("3분기")
                                        .otherwise("4분기"),
                                corpFinance.stockCode,
                                corpFinance.capital,
                                corpFinance.totalAssets,
                                corpFinance.totalDebt,
                                corpFinance.totalEquity,
                                corpFinance.revenue,
                                corpFinance.netIncome,
                                corpFinance.operatingProfit
                        )
                )
                .from(corpFinance)
                .where(corpFinance.financeId.eq(id))
                .orderBy(createOrderSpecifier(orderList))
                .fetch();

        return  results;
    }


    private OrderSpecifier[] createOrderSpecifier(List<String> keyList) {

        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();

        for(String key : keyList){

            if(key.equals("PSR")){
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, corpFinance.PSR));
            }else if(key.equals("POR")){
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, corpFinance.POR));
            }else if(key.equals("PER")){
                orderSpecifiers.add(new OrderSpecifier(Order.ASC, corpFinance.PER));
            }

        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
