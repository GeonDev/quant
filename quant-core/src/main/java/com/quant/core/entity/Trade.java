package com.quant.core.entity;

import com.quant.core.enums.TradingType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_STOCK_TRADE")
public class Trade {

    @Id
    @GeneratedValue(generator = "key-generator")
    @GenericGenerator(name = "key-generator",
            parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "TR"),
            strategy = "com.quant.core.config.KeyGenerator")
    String tradeId;

    String userKey;

    //주식 코드
    String stockCode;

    //거래 갱신일
    LocalDate tradingDt;

    //전체 투입 금액
    Integer totalAsset;

    //1주당 평균 가격
    Double average;

    //주식 개수
    Integer stockCount;
    
}
