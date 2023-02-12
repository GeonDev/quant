package com.stock.quant.api.model.dataGo.base;

import lombok.Data;

@Data
public class DataResponse<T> {
    DataHeader header;
    DataBody<T> body;
}


