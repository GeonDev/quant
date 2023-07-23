package com.quant.stock.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quant.core.consts.ApplicationConstants;
import com.quant.core.entity.*;
import com.quant.core.enums.CorpState;
import com.quant.core.enums.QuarterCode;
import com.quant.core.enums.PriceType;
import com.quant.core.enums.StockType;
import com.quant.core.repository.*;
import com.quant.core.utils.CommonUtils;
import com.quant.core.utils.DateUtils;
import com.quant.stock.model.dart.DartBase;
import com.quant.stock.model.dart.FinanceItem;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class StockService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UserInfoRepository userInfoRepository;
    private final CorpInfoRepository corpInfoRepository;
    private final StockPriceRepository stockPriceRepository;
    private final CorpFinanceRepository financeRepository;
    private final StockAverageRepository stockAverageRepository;

    @Value("${signkey.data-go}")
    String apiKey;

    @Value("${signkey.dart}")
    String dartKey;

    @Value("${file.path}")
    String filePath;


    public String setUserInfo(String email) {
        UserInfo info = UserInfo.builder()
                .email(email)
                .build();

        return userInfoRepository.save(info).getUserKey();
    }


    //주식 시장 활성일 체크 -> 활성일 일 경우 주식 시세 받기
    public void getKrxDailyInfo(LocalDate targetDate) {

        logger.debug("getKrxStockPrice date : {}", targetDate);

        //공공정보 API는 1일전 데이터가 최신, 전일 데이터는 오후 1시에 갱신, 월요일에 금요일 데이터 갱신
        if (targetDate.getDayOfWeek().getValue() == 1) {
            targetDate = targetDate.minusDays(3);
        } else {
            targetDate = targetDate.minusDays(1);
        }

        try {
            UriComponents uri = UriComponentsBuilder
                    .newInstance()
                    .scheme("http")
                    .host(ApplicationConstants.API_GO_URL)
                    .path(ApplicationConstants.KAI_REST_DATE_URL)
                    .queryParam("solYear", targetDate.getYear())
                    .queryParam("solMonth", targetDate.getMonthValue() < 10 ? "0" + targetDate.getMonthValue() : targetDate.getMonthValue())
                    .queryParam("_type", "json")
                    .queryParam("ServiceKey", URLDecoder.decode(apiKey, "UTF-8"))
                    .queryParam("numOfRows", 10)
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(result.getBody());
            JSONObject response = (JSONObject) object.get("response");
            JSONObject header = (JSONObject) response.get("header");
            JSONObject body = (JSONObject) response.get("body");


            boolean isDayOff = false;

            logger.debug("REST item {}", body.get("items").toString());
            if (!body.get("items").toString().equals("")) {
                JSONObject items = (JSONObject) body.get("items");
                JSONArray itemList = (JSONArray) items.get("item");

                if (object != null && header.get("resultMsg").toString().equals(ApplicationConstants.REQUEST_MSG)) {
                    for (int i = 0; i < itemList.size(); i++) {
                        JSONObject item = (JSONObject) itemList.get(i);

                        if (DateUtils.toStringLocalDate(item.get("locdate").toString()).isEqual(targetDate)) {
                            isDayOff = true;
                            break;
                        }
                    }
                }
            }

            if (!isDayOff) {
                getStockPrice(StockType.KOSPI.name(), DateUtils.toLocalDateString(targetDate), 1, 0, 0);
                getStockPrice(StockType.KOSDAQ.name(), DateUtils.toLocalDateString(targetDate), 1, 0, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //주식 시세 받아오기
    public void getStockPrice(String marketType, String basDt, int pageNum, int totalCount, int currentCount) {
        if (totalCount != 0 && totalCount <= currentCount) {
            return;
        }

        try {
            UriComponents uri = UriComponentsBuilder
                    .newInstance()
                    .scheme("http")
                    .host(ApplicationConstants.API_GO_URL)
                    .path(ApplicationConstants.KRX_STOCK_VALUE_URI)
                    .queryParam("serviceKey", URLDecoder.decode(apiKey, "UTF-8"))
                    .queryParam("numOfRows", ApplicationConstants.PAGE_SIZE)
                    .queryParam("pageNo", pageNum)
                    .queryParam("resultType", "json")
                    .queryParam("mrktCls", marketType)
                    .queryParam("basDt", basDt)
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);

            JSONParser parser = new JSONParser();
            JSONObject object = (JSONObject) parser.parse(result.getBody());
            JSONObject response = (JSONObject) object.get("response");
            JSONObject header = (JSONObject) response.get("header");
            JSONObject body = (JSONObject) response.get("body");

            totalCount = Integer.parseInt(body.get("totalCount").toString());

            if (header.get("resultMsg").toString().equals(ApplicationConstants.REQUEST_MSG)) {

                if (!body.get("items").toString().equals("")) {
                    JSONObject items = (JSONObject) body.get("items");
                    JSONArray itemList = (JSONArray) items.get("item");


                    for (int i = 0; i < itemList.size(); i++) {
                        JSONObject item = (JSONObject) itemList.get(i);
                        StockPrice price = new StockPrice();

                        price.setStockCode(item.get("srtnCd").toString());
                        price.setMarketCode(marketType);
                        price.setBasDt(DateUtils.toLocalDate(basDt));
                        price.setVolume(Integer.parseInt(item.get("trqu").toString()));
                        price.setStartPrice(Integer.parseInt(item.get("mkp").toString()));
                        price.setEndPrice(Integer.parseInt(item.get("clpr").toString()));
                        price.setLowPrice(Integer.parseInt(item.get("lopr").toString()));
                        price.setHighPrice(Integer.parseInt(item.get("hipr").toString()));
                        price.setDailyRange(Double.parseDouble(item.get("vs").toString()));
                        price.setDailyRatio(Double.parseDouble(item.get("fltRt").toString()));
                        price.setStockTotalCnt(Long.parseLong(item.get("lstgStCnt").toString()));
                        price.setMarketTotalAmt(Long.parseLong(item.get("mrktTotAmt").toString()));

                        stockPriceRepository.save(price);
                        currentCount += 1;
                    }
                    getStockPrice(marketType, basDt, pageNum + 1, totalCount, currentCount);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //상장 회사 고유 정보 받아오기
    public void getDartCorpCodeInfo() {
        UriComponents uri = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(ApplicationConstants.DART_API_URL)
                .path(ApplicationConstants.DART_CORP_CODE_URI)
                .queryParam("crtfc_key", dartKey)
                .build();


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, byte[].class);

        try {
            File lOutFile = new File(filePath + "temp.zip");
            FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
            lFileOutputStream.write(response.getBody());
            lFileOutputStream.close();

            CommonUtils.unZip(filePath + "temp.zip", filePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse(filePath + "CORPCODE.xml");
            NodeList corpList = document.getElementsByTagName("list");

            List<CorpInfo> codeList = new ArrayList<>();

            for (int i = 0; i < corpList.getLength(); i++) {
                Element corp = (Element) corpList.item(i);
                if (corp != null) {
                    //상장된 회사만 저장
                    if (getValue("stock_code", corp) != null && StringUtils.hasText(getValue("stock_code", corp))) {
                        //스팩, 투자회사등 제외
                        if (!getValue("corp_name", corp).contains("스팩") &&
                                !getValue("corp_name", corp).contains("기업인수") &&
                                !getValue("corp_name", corp).contains("투자회사") &&
                                !getValue("corp_name", corp).contains("인베스트먼트") &&
                                !getValue("corp_name", corp).contains("매니지먼트") &&
                                !getValue("corp_name", corp).contains("유한회사") &&
                                !getValue("corp_name", corp).contains("유한공사") &&
                                !getValue("corp_name", corp).contains("유동화전문") &&
                                !getValue("corp_name", corp).contains("리미티드") &&
                                !getValue("corp_name", corp).contains("펀드") &&
                                !getValue("corp_name", corp).toLowerCase(Locale.ROOT).contains("llc") &&
                                !getValue("corp_name", corp).toLowerCase(Locale.ROOT).contains("limited") &&
                                !getValue("corp_name", corp).toLowerCase(Locale.ROOT).contains("ltd") &&
                                !getValue("corp_name", corp).toLowerCase(Locale.ROOT).contains("fund")) {

                            CorpInfo code = corpInfoRepository.findById(getValue("corp_code", corp)).orElseGet(() -> new CorpInfo().builder()
                                    .corpCode(getValue("corp_code", corp))
                                    .stockCode(getValue("stock_code", corp))
                                    .corpName(getValue("corp_name", corp))
                                    .state(CorpState.ACTIVE)
                                    .build());

                            //체크 일자 업데이트
                            code.setCheckDt(LocalDate.now());
                            codeList.add(code);
                        }
                    }
                }
            }
            corpInfoRepository.saveAll(codeList);

            //회사 목록 업데이트 후 일자가 변경되지 않은 회사들 확인
            List<CorpInfo> unCheckedCorpList = corpInfoRepository.findByCheckDtBefore(LocalDate.now());

            for (CorpInfo corp : unCheckedCorpList) {
                corp.setState(CorpState.DEL);
            }

            corpInfoRepository.saveAll(unCheckedCorpList);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getTextContent().trim();
    }

    //회사 목록 전체의 재무재표 업데이트
    public void setCorpFinanceInfo() {
        List<CorpInfo> infoList = corpInfoRepository.findByState(CorpState.ACTIVE);

        String year = Integer.toString(LocalDate.now().getYear());

        for (CorpInfo info : infoList) {

            try {
                //1분기 보고서
                setCorpFinanceInfo(info.getCorpCode(), year, QuarterCode.Q1);

                //반기 보고서
                setCorpFinanceInfo(info.getCorpCode(), year, QuarterCode.Q2);

                //3분기 보고서
                setCorpFinanceInfo(info.getCorpCode(), year, QuarterCode.Q3);

                //사업 보고서
                setCorpFinanceInfo(info.getCorpCode(), year, QuarterCode.Q4);

                //오픈 다트 정책상 초당 16건 이하의 요청만 허용함으로 0.3초(초당 12회) 슬립추가
                TimeUnit.MICROSECONDS.sleep(300);

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    // 상장회사 재무 정보 다운로드
    @Transactional
    public void setCorpFinanceInfo(String corpCode, String year, QuarterCode quarter) {
        UriComponents uri = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(ApplicationConstants.DART_API_URL)
                .path(ApplicationConstants.DART_STOCK_FINANCE_URI)
                .queryParam("crtfc_key", dartKey)
                .queryParam("corp_code", corpCode)
                .queryParam("bsns_year", year)
                .queryParam("reprt_code", quarter.getCode())
                .build();

        logger.debug("DART URL : {}", uri);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
            DartBase<FinanceItem> response = mapper.readValue(result.getBody(), DartBase.class);

            if (response.getStatus().equals("000")) {

                if (financeRepository.findByCorpCodeAndRceptNoAndYearCode(corpCode, quarter.getCode(), year) == null) {

                    List<CorpFinance> financeList = null;

                    List<FinanceItem> financeSrcList = mapper.convertValue(response.getList(), new TypeReference<List<FinanceItem>>() {
                    });

                    CorpFinance finance = new CorpFinance();

                    finance.setRceptNo(financeSrcList.get(0).getRcept_no());
                    finance.setYearCode(year);
                    finance.setStockCode(financeSrcList.get(0).getStock_code());
                    finance.setCorpCode(financeSrcList.get(0).getCorp_code());


                    for (FinanceItem item : financeSrcList) {
                        if (item.getFs_div().equals("OFS")) {
                            Long value = Long.parseLong(item.getThstrm_amount().replace(",", ""));

                            if (item.getSj_div().equals("IS")) {
                                if (item.getAccount_nm().equals("매출액")) {
                                    finance.setRevenue(value);

                                    String[] data = item.getThstrm_dt().replace(".", "").split(" ~ ");
                                    finance.setStartDt(DateUtils.toLocalDate(data[0]));
                                    finance.setEndDt(DateUtils.toLocalDate(data[1]));

                                } else if (item.getAccount_nm().equals("영업이익")) {
                                    finance.setOperatingProfit(value);
                                } else if (item.getAccount_nm().equals("당기순이익")) {
                                    finance.setNetIncome(value);
                                }
                            } else if (item.getSj_div().equals("BS")) {
                                if (item.getAccount_nm().equals("부채총계")) {
                                    finance.setTotalDebt(value);
                                } else if (item.getAccount_nm().equals("자본금")) {
                                    finance.setCapital(value);
                                } else if (item.getAccount_nm().equals("이익잉여금")) {
                                    finance.setEarnedSurplus(value);
                                } else if (item.getAccount_nm().equals("자본총계")) {
                                    finance.setTotalEquity(value);
                                } else if (item.getAccount_nm().equals("자산총계")) {
                                    finance.setTotalAssets(value);
                                }
                            }

                            //YOY, QoQ 계산
                            setFinanceRatio(corpCode, year, quarter, finance);

                            //PSR PER 계산
                            setFinanceIndicators(year, quarter, finance);

                            financeList.add(finance);
                        }
                    }

                    financeRepository.saveAll(financeList);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFinanceRatio(String corpCode, String year, QuarterCode quarter, CorpFinance finance) {
        //전년도 재무정보
        CorpFinance byFinance = financeRepository.findByCorpCodeAndRceptNoAndYearCode(corpCode, quarter.getCode(), String.valueOf(Integer.parseInt(year) - 1));
        Double yoy = ((finance.getRevenue().doubleValue() - byFinance.getRevenue().doubleValue()) - 1.0) * 100;
        finance.setYOY(yoy);

        //전분기 재무정보 가지고 오기
        if (quarter.equals(QuarterCode.Q1)) {
            //전분기 재무정보 (1분기 값은 작년정보)
            CorpFinance bqFinance = financeRepository.findByCorpCodeAndRceptNoAndYearCode(corpCode, quarter.getBefore(), String.valueOf(Integer.parseInt(year) - 1));
            Double qoq = ((finance.getRevenue().doubleValue() - bqFinance.getRevenue().doubleValue()) - 1.0) * 100;
            finance.setQOQ(qoq);
        } else {
            //전분기 재무정보
            CorpFinance bqFinance = financeRepository.findByCorpCodeAndRceptNoAndYearCode(corpCode, quarter.getBefore(), year);
            Double qoq = ((finance.getRevenue().doubleValue() - bqFinance.getRevenue().doubleValue()) - 1.0) * 100;
            finance.setQOQ(qoq);
        }
    }

    public void setFinanceIndicators(String year, QuarterCode quarter, CorpFinance finance) {
        //분기 데이터의 마지막일자 시장가 불러오기
        StockPrice nowPrice = stockPriceRepository.findTopByStockCodeAndBasDt(finance.getStockCode(), finance.getEndDt());

        finance.setPSR(nowPrice.getMarketTotalAmt().doubleValue() / finance.getRevenue().doubleValue());
        finance.setPBR(nowPrice.getMarketTotalAmt().doubleValue() / finance.getTotalEquity().doubleValue());
        finance.setPER(nowPrice.getMarketTotalAmt().doubleValue() / finance.getNetIncome().doubleValue());
    }


    //주식의 가격 평균 배치
    public void setStockPriceAverage(LocalDate targetDate) {
        if (targetDate == null) {
            targetDate = LocalDate.now();
        }

        List<CorpInfo> targetCorp = corpInfoRepository.findByState(CorpState.ACTIVE);

        for (CorpInfo corp : targetCorp) {
            stockPriceAverage(corp.getStockCode(), targetDate, PriceType.DAY5);
            stockPriceAverage(corp.getStockCode(), targetDate, PriceType.DAY20);
            stockPriceAverage(corp.getStockCode(), targetDate, PriceType.DAY60);
            stockPriceAverage(corp.getStockCode(), targetDate, PriceType.DAY120);
            stockPriceAverage(corp.getStockCode(), targetDate, PriceType.DAY200);
        }
    }

    @Transactional
    public void stockPriceAverage(String stockCode, LocalDate targetDate, PriceType priceType) {

        PageRequest pageRequest = PageRequest.of(0, priceType.getValue(), Sort.by("BAS_DT").descending());
        List<StockPrice> priceList = stockPriceRepository.findByStockCodeAndBasDtBefore(stockCode, targetDate, pageRequest);


        StockAverage average = new StockAverage();
        average.setStockCode(stockCode);
        average.setTarDt(targetDate);
        average.setPriceType(priceType);

        Integer totalPrice = 0;
        if (priceList.size() == priceType.getValue()) {
            for (StockPrice price : priceList) {
                totalPrice += price.getEndPrice();
            }
            average.setPrice(totalPrice / priceType.getValue());
        } else {
            //기간 개수가 모자르다면 평균을 0으로 처리
            average.setPrice(0);
        }

        stockAverageRepository.save(average);
    }

}
