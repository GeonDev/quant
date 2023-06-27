package com.quant.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CorpType {
    BANK("은행주"),
    CHINA("중국주식"),
    HOLDING("지주사");

    private String desc;

}
