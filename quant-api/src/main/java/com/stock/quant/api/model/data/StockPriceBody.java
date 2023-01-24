package com.stock.quant.api.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "증권 정보 API 바디")
public class StockPriceBody {

    @Schema(description = "한 페이지의 결과 수")
    String numOfRows;

    @Schema(description = "현재 조회된 데이터의 페이지 번호")
    String pageNo;

    @Schema(description = "전체 데이터의 총 수")
    String totalCount;

    List<StockPriceItem> items;
}
