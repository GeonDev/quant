package com.quant.stock.model.dart;

import lombok.Data;

import java.util.List;

@Data
public class DartBase<T> {

    String status;

    String message;

    List<T> list;

}
