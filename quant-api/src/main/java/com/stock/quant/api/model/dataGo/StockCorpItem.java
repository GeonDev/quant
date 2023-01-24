package com.stock.quant.api.model.dataGo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "상장 법인 정보")
public class StockCorpItem {

    String basDt;
    @Schema(description = "종목 코드보다 짧으면서 유일성이 보장되는 코드(6자리)")
    String srtnCd;

    @Schema(description = "국제 채권 식별 번호. 유가증권(채권)의 국제인증 고유번호")
    String isinCd;

    @Schema(description = "주식의 시장 구분", allowableValues = {"KOSPI", "KOSDAQ", "KONEX"})
    String mrktCtg;

    String itmsNm;

    @Schema(description = "국제 채권 식별 번호. 유가증권(채권)의 국제인증 고유번호")
    String crno;

    String corpNm;


}
