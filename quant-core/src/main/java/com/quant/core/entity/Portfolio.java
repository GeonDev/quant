package com.quant.core.entity;

import com.quant.core.enums.AmtRange;
import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "TB_STOCK_PORTFOLIO")
public class Portfolio {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long portfolioId;

    String userKey;

    //최소 모멘텀
    Integer momentumScore;

    //포트폴리오당 최대 종목개수
    Integer stockCount;

    //시총 범위
    AmtRange range;

    //지표 (ex per,psr ...)
    String Indicator;

    //리벨런싱 (ex 1,6,12 ...)
    String reBalance;
}
