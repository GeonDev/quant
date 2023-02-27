package com.stock.quant.api.model.dataGo.base;

import lombok.Data;

@Data
public class ApiResponse <T>{
    DataResponse<T> response;
}
