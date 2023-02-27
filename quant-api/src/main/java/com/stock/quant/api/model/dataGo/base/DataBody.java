package com.stock.quant.api.model.dataGo.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DataBody<T> {

    @Schema(description = "한 페이지의 결과 수")
    String numOfRows;

    @Schema(description = "현재 조회된 데이터의 페이지 번호")
    String pageNo;

    @Schema(description = "전체 데이터의 총 수")
    String totalCount;

    DataItem<T> items;
}
