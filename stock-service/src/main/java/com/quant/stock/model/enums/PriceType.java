package com.quant.stock.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PriceType {
    FIVE(5),
    TWENTY(20),
    SIXTY(60);

    private int value;

}
