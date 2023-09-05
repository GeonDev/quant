package com.quant.core.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum AmtRange {
    UPPER200("시가총액 상위 200위"),
    LOWER20("시가총액 하위 20%"),
    ALL("상장 주식 전체");

    private String desc;
}
