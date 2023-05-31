package com.quant.stock.entity;

import lombok.Data;
import lombok.NonNull;

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

    @NonNull
    @Enumerated(EnumType.STRING)
    private PriceType priceType;

    //시초가
    Integer price;




}
