package com.quant.core.entity;

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

    Integer stockCount;

    Integer marketCap;

    String orderSet;

}
