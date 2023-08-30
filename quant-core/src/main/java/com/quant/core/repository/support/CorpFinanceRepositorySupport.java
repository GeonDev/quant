package com.quant.core.repository.support;

import com.quant.core.entity.CorpFinance;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
