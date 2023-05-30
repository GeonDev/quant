package com.quant.stock.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "TB_STOCK_PRICE")
public class StockPrice {

    @Id
    String stockCode;

    //주식시장 구분
    String marketCode;

    //기준일
    LocalDate basDt;

    //채결수량
    Integer volume;

    //시초가
    Integer startPrice;

    //종가
    Integer endPrice;

    //일간 최고가
    Integer highPrice;

    //일간 최저가
    Integer lowPrice;

    //전일 대비 등락
    Double dailyRange;

    //등락율
    Double dailyRatio;

    //상장 주식수
    Long stockTotalCnt;

    // 종가 * 상장 주식수
    Long marketTotalAmt;


}
