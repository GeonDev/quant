package com.quant.core.repository.support;

import com.quant.core.entity.StockPrice;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import static com.quant.core.entity.QStockPrice.stockPrice;

@Repository
public class StockPriceRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory queryFactory;

    public StockPriceRepositorySupport(JPAQueryFactory queryFactory) {
        super(StockPrice.class);
        this.queryFactory = queryFactory;
    }

    //현재 보유중인 주식 개수를 반환
    public Integer findByStockAverage(String stockCode, LocalDate targetDate, Integer count ){
        List<Integer> result = queryFactory.select(
                stockPrice.endPrice)
                .from(stockPrice)
                .where(stockPrice.stockCode.eq(stockCode) ,stockPrice.basDt.before(targetDate.plusDays(1)))
                .limit(count)
                .fetch();

        if(result.size() == count){
           return result.stream().mapToInt(Integer::intValue).sum()/count;
        }

        return  0;
    }

}
