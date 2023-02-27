package com.stock.quant.api.consts;

public class ApplicationConstants {

    private ApplicationConstants() {
        throw new IllegalAccessError("어플리케이션에서 공통으로 사용되는 상수 클래스로 생성자를 사용할 수 없습니다.");
    }

    public static final int PAGE_SIZE = 1000;

    public static final String API_GO_URL = "apis.data.go.kr";

    public static final String DART_API_URL = "opendart.fss.or.kr";

    public static final String REQUEST_MSG = "NORMAL SERVICE.";

    public static final String KAI_REST_DATE_URL = "/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

    public static final String KRX_STOCK_LIST_URI = "/1160100/service/GetKrxListedInfoService/getItemInfo";

    public static final String KRX_STOCK_FINANCE_URI = "/1160100/service/GetFinaStatInfoService";

    public static final String KRX_STOCK_VALUE_URI = "/1160100/service/GetStockSecuritiesInfoService";

    public static final String DART_STOCK_FINANCE_URI = "/api/fnlttSinglAcnt.json";



}
