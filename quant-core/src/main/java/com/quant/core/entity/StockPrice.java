package com.quant.core.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.hibernate.annotations.Parameter;


import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "TB_STOCK_PRICE")
public class StockPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long priceId;

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

    //종가 - (n 개월전 종가)의 +/- 값, 7이상일때 상승
    Integer momentum;

    @PrePersist
    public void prePersist() {
        this.momentum = (this.momentum == null ? 0 : this.momentum);
    }

}
