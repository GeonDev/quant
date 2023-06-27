package com.quant.core.entity;

import com.quant.core.enums.PriceType;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "TB_STOCK_AVERAGE")
public class StockAverage implements Serializable {

    @Id
    String stockCode;

    //기준일
    LocalDate tarDt;

    @Enumerated(EnumType.STRING)
    PriceType priceType;

    //평균가
    Integer price;
}
