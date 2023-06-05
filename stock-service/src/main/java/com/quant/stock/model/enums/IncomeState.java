package com.quant.stock.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IncomeState {

    SURPLUS("흑자"),
    DEFLICIT("적자");

    private String desc;
}
