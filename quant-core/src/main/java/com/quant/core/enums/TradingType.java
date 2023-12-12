package com.quant.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TradingType {
    BUY("매수"),
    SELL("매도"),
    DEPOSIT("입금"),
    WITHDRAW("출금");

    private String desc;

}
