package com.stock.quant.api.model.dataGo.base;

import lombok.Data;

import java.util.List;

@Data
public class DataItem <T>{
    List<T> item;
}
