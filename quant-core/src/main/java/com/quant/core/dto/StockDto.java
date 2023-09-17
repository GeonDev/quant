package com.quant.core.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class StockDto implements Serializable {

    String stockCode;

    Integer endPrice;
}
