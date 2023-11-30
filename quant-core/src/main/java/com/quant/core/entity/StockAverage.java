package com.quant.core.entity;

import com.quant.core.enums.PriceType;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@Table(name = "TB_STOCK_AVERAGE")
public class StockAverage implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long averageId;

    String stockCode;

    //기준일
    LocalDate tarDt;

    //평균 타입 (5일, 10일, 20일 등 )
    @Enumerated(EnumType.STRING)
    PriceType priceType;

    //평균가
    Integer price;
}
