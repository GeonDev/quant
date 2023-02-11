package com.stock.quant.api.model.dataGo.base;

import lombok.Data;

import java.util.List;

@Data
public class DataResponse<T> {
    DataHeader header;
    DataBody<List<T>> body;
}


