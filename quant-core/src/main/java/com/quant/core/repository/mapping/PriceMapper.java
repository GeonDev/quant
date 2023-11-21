package com.quant.core.repository.mapping;

import java.time.LocalDate;

public interface PriceMapper {
    Integer getEndPrice();
    Long getMarketTotalAmt();
    LocalDate getBasDt();
}
