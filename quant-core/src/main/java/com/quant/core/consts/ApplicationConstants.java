package com.quant.core.consts;

public class ApplicationConstants {

    private ApplicationConstants() {
        throw new IllegalAccessError("어플리케이션에서 공통으로 사용되는 상수 클래스로 생성자를 사용할 수 없습니다.");
    }

    public static final String SPLIT_KEY = ",";

    //OPEN DART 제공 연도(최소 2015년)
    public static final int LIMIT_YEAR = 2020;

    //공공 데이터 포탈의 최대 열 개수(최대 1000 ~ 1500)
    public static final int PAGE_SIZE = 1000;

    public static final String BAN_STOCK_NAME_LIST = "스팩|기업인수|투자회사|인베스트먼트|매니지먼트|유한회사|유한공사|유동화전문|리미티드|펀드|llc|limited|ltd|fund";

    public static final String API_GO_URL = "apis.data.go.kr";

    public static final String DART_API_URL = "opendart.fss.or.kr";

    public static final String REQUEST_MSG = "NORMAL SERVICE.";

    public static final String KAI_REST_DATE_URL = "/B090041/openapi/service/SpcdeInfoService/getRestDeInfo";

    public static final String KRX_STOCK_LIST_URI = "/1160100/service/GetKrxListedInfoService/getItemInfo";

    public static final String KRX_STOCK_FINANCE_URI = "/1160100/service/GetFinaStatInfoService";

    public static final String KRX_STOCK_VALUE_URI = "/1160100/service/GetStockSecuritiesInfoService/getStockPriceInfo";

    public static final String DART_STOCK_FINANCE_SINGLE_URI = "/api/fnlttSinglAcnt.json";

    public static final String DART_STOCK_FINANCE_MULTI_URI = "/api/fnlttMultiAcnt.json";

    public static final String DART_CORP_CODE_URI = "/api/corpCode.xml";


}
