package com.quant.core.entity;

import com.quant.core.enums.TradingType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "TB_STOCK_PORTFOLIO")
public class Portfolio {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long portfolioId;

    String userKey;

    //매매, 매수 구분
    @Enumerated(EnumType.STRING)
    TradingType trading;

    //거래일
    LocalDate tradingDt;

    //1주당 평균 가격
    Integer average;

    //매매/매도 개수
    Integer stockCount;
    
}
