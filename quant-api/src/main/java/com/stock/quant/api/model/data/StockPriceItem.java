package com.stock.quant.api.model.data;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "증권 정보 API 아이템")
public class StockPriceItem {

    @Schema(description = "유가증권 국제인증 고유번호 코드 이름")
    String itemNm;

    @Schema(description = "주식의 시장 구분", allowableValues = {"KOSPI", "KOSDAQ", "KONEX"})
    String mrkCtg;

    @Schema(description = "정규시장의 매매시간종료시까지 형성되는 최종가격")
    String clpr;

    @Schema(description = "전일 대비 등락")
    String vs;

    @Schema(description = "전일 대비 등락에 따른 비율")
    Integer fltRt;

    @Schema(description = "정규시장의 매매시간개시후 형성되는 최초가격")
    Integer mkp;

    @Schema(description = "하루 중 가격의 최고치")
    Integer hipr;

    @Schema(description = "하루 중 가격의 최저치")
    Integer lopr;

    @Schema(description = "체결수량의 누적 합계")
    Integer trqu;

    @Schema(description = "거래건 별 체결가격 * 체결수량의 누적 합계")
    Integer trprc;

    @Schema(description = "종목의 상장주식수")
    Integer lstgStCnt;

    @Schema(description = "기준일자")
    String basDt;

    @Schema(description = "종목 코드보다 짧으면서 유일성이 보장되는 코드(6자리)")
    String srtnCd;

    @Schema(description = "국제 채권 식별 번호. 유가증권(채권)의 국제인증 고유번호")
    String isinCd;

    @Schema(description = "종가 * 상장주식수")
    Integer mrktTotAmt;
}
