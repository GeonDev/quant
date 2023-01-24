package com.stock.quant.api.model.dart;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "DART API Item")
public class FinanceItem {

    @Schema(description = "접수번호(14자리)")
    String rcept_no;

    @Schema(description = "사업 연도\t2019")
    String bsns_year;

    @Schema(description = "종목 코드\t상장회사의 종목코드(6자리)")
    String stock_code;

    @Schema(description = "보고서 코드" , allowableValues = {"11013" , "11012", "11014"  ,"11011"})
    String reprt_code;

    @Schema(description = "계정명")
    String account_nm;

    @Schema(description = "개별/연결구분 CFS:연결재무제표, OFS:재무제표" )
    String fs_div;

    @Schema(description = "개별/연결명\tex) 연결재무제표 또는 재무제표 출력")
    String fs_nm;

    @Schema(description = "\t재무제표구분\tBS:재무상태표, IS:손익계산서")
    String sj_div;

    @Schema(description = "\t재무제표명\tex) 재무상태표 또는 손익계산서 출력")
    String sj_nm;

    @Schema(description = "\t당기명\tex) 제 13 기 3분기말")
    String thstrm_nm;

    @Schema(description = "\t당기일자\tex) 2018.09.30 현재")
    String thstrm_dt;

    @Schema(description = "\t당기금액\t9,999,999,999")
    String thstrm_amount;

    @Schema(description = "당기누적금액")
    String thstrm_add_amount;

    @Schema(description = "\t전기명\tex) 제 12 기말")
    String frmtrm_nm;

    @Schema(description = "\t전기일자\tex) 2017.01.01 ~ 2017.12.31")
    String frmtrm_dt;

    @Schema(description = "\t전기금액\t9,999,999,999")
    String frmtrm_amount;

    @Schema(description = "\t전기누적금액\t9,999,999,999")
    String frmtrm_add_amount;

    @Schema(description = "\t전전기명\tex) 제 11 기말(※ 사업보고서의 경우에만 출력)")
    String bfefrmtrm_nm;

    @Schema(description = "\t전전기일자\tex) 2016.12.31 현재(※ 사업보고서의 경우에만 출력)")
    String bfefrmtrm_dt;

    @Schema(description = "\t전전기금액\t9,999,999,999(※ 사업보고서의 경우에만 출력)")
    String bfefrmtrm_amount;

    @Schema(description = "\t계정과목 정렬순서\t계정과목 정렬순서")
    String ord;

    @Schema(description = "\t통화 단위\t통화 단위")
    String currency;
}
