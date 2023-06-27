package com.quant.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CorpState {
    ACTIVE("활성"),
    HALT("거래정지"),
    caution("투자주의"),
    DEL("상장폐지");

    private String desc;

}
