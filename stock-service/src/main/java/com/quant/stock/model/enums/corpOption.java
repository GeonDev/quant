package com.quant.stock.model.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum corpOption {
    BANK("은행주"),
    CHINA("중국주식"),
    HOLDING("지주사"),
    ETC("기타");

    private String desc;

}
