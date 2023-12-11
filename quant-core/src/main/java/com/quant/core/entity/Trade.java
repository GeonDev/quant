package com.quant.core.entity;

import com.quant.core.enums.TradingType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_STOCK_TRADE_HISTORY")
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long tradeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portfolio_id")
    @ToString.Exclude
    Portfolio portfolio;

    //주식 코드
    String stockCode;

    //거래 갱신일
    LocalDate tradingDt;

    //거래 금액
    Integer price;

    //주식 개수
    Integer stockCount;

    // 주식 개수 * 거래 금액
    Integer totalValue;

    @Enumerated(EnumType.STRING)
    TradingType tradeType;
    
}
