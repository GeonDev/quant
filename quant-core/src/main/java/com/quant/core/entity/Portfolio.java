package com.quant.core.entity;

import com.quant.core.enums.AmtRange;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Table(name = "TB_STOCK_PORTFOLIO")
public class Portfolio {

    @Id
    @GeneratedValue(generator = "key-generator")
    @GenericGenerator(name = "key-generator",
            parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "PF"),
            strategy = "com.quant.core.config.KeyGenerator")
    String portfolioId;

    @ManyToOne
    @JoinColumn(name = "user_key")
    UserInfo userInfo;

    //전체 투자금
    Integer totalValue;

    //최소 모멘텀
    Integer momentumScore;

    //포트폴리오당 최대 종목개수
    Integer stockCount;

    //손절 퍼센트
    Integer lossCut;

    //시총 범위
    @Enumerated(EnumType.STRING)
    AmtRange ranges;

    //지표 (ex per,psr ...)
    String indicator;

    //리벨런싱 (ex 1,6,12 ...)
    String rebalance;

    String comment;
}
