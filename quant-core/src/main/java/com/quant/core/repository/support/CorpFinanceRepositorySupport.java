package com.quant.core.repository.support;

import com.quant.core.entity.CorpFinance;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static com.quant.core.entity.QCorpFinance.corpFinance;

@Repository
public class CorpFinanceRepositorySupport extends QuerydslRepositorySupport {

    @Autowired
    private final JPAQueryFactory queryFactory;

    public CorpFinanceRepositorySupport(JPAQueryFactory queryFactory) {
        super(CorpFinance.class);
        this.queryFactory = queryFactory;
    }

}
