package com.quant.stock.model;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockOrder implements Comparable<StockOrder>{

    StockOrder stock;

    Integer order;

    @Override
    public int compareTo(StockOrder o) {
        return o.getOrder() - getOrder();
    }
}
