package com.quant.stock.model;

import com.quant.core.dto.StockDto;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockOrder implements Comparable<StockOrder>{

    StockDto stock;

    Integer order;

    @Override
    public int compareTo(StockOrder o) {
        return o.getOrder() - getOrder();
    }
}
