package com.stock.quant.api.model.dataGo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "날짜 정보 아이템 처리")
public class StockDateItem {

    @Schema(description = "종류")
    String dateKind;

    @Schema(description = "명칭")
    String dateName;

    @Schema(description = "공공기관 휴일 여부 (Y/N)")
    String isHoliday;

    @Schema(description = "날짜")
    String locdate;

    @Schema(description = "순번")
    Integer seq;
}
