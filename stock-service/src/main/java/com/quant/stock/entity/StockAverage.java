package com.quant.stock.entity;

import com.quant.stock.model.enums.PriceType;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "TB_STOCK_PRICE_AVERAGE")
public class StockAverage {

    @Id
    String stockCode;

    //기준일
    LocalDate tarDt;

    @Enumerated(EnumType.STRING)
    PriceType priceType;

    //시초가
    Integer price;
}
