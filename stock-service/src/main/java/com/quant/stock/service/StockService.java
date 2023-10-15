package com.quant.stock.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quant.core.consts.ApplicationConstants;
import com.quant.core.dto.StockDto;
import com.quant.core.entity.*;
import com.quant.core.enums.*;
import com.quant.core.exception.InvalidRequestException;
import com.quant.core.repository.mapping.CorpCodeMapper;
import com.quant.core.repository.mapping.PriceMapper;
import com.quant.core.dto.RecommendDto;
import com.quant.core.repository.*;
import com.quant.core.repository.support.CorpFinanceRepositorySupport;
import com.quant.core.utils.CommonUtils;
import com.quant.core.utils.DateUtils;
import com.quant.stock.model.EmailMessage;
import com.quant.stock.model.StockOrder;
import com.quant.stock.model.dart.DartBase;
import com.quant.stock.model.dart.FinanceItem;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.tinylog.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class StockService {

    private final EmailService emailService;

    private final TradeRepository tradeRepository;
    private final UserInfoRepository userInfoRepository;
    private final CorpInfoRepository corpInfoRepository;
    private final StockPriceRepository stockPriceRepository;
    private final CorpFinanceRepository financeRepository;
    private final StockAverageRepository stockAverageRepository;
    private final PortfolioRepository portfolioRepository;
    private final CorpFinanceRepositorySupport financeSupport;

    @Value("${signkey.path}")
    String signkey;

    @Value("${file.path}")
    String filePath;

    @Transactional
    public void setUserInfo(String email) {

        if(!email.matches("^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$")){
            throw new InvalidRequestException("이메일 형식이 맞지 않습니다.");
        }

        if (userInfoRepository.countByEmail(email) == 0) {


            UserInfo info = UserInfo.builder()
                    .email(email)
                    .build();
            userInfoRepository.save(info);


            emailService.sendMail(EmailMessage.builder()
                            .to(email)
                            .subject("[Quent] 신규 이메일등록")
                            .message("신규 등록 되었습니다.")
                    .build());

        } else {
            throw new InvalidRequestException("중복된 이메일 있음");
        }
    }


    //주식 시장 활성일 체크 -> 활성일 일 경우 주식 시세 받기
    //@Async
    public void getKrxDailyInfo(LocalDate targetDate) {
        //주말은 무시
        if (targetDate.getDayOfWeek().getValue() == 0 || targetDate.getDayOfWeek().getValue() == 6) {
            return;
        }

        //공공정보 API는 1일전 데이터가 최신, 전일 데이터는 오후 1시에 갱신, 월요일에 금요일 데이터 갱신
        if (targetDate.getDayOfWeek().getValue() == 1) {
            targetDate = targetDate.minusDays(3);
        } else {
            targetDate = targetDate.minusDays(1);
        }

        try {
            if (!checkIsDayOff(targetDate)) {
                getStockPrice(StockType.KOSPI.name(), DateUtils.toLocalDateString(targetDate), 1, 0, 1);
                getStockPrice(StockType.KOSDAQ.name(), DateUtils.toLocalDateString(targetDate), 1, 0, 1);
            }
        } catch (Exception e) {
            Logger.error("{}", e);
        }
    }

    //공휴일 체크 기능
    private boolean checkIsDayOff(LocalDate targetDate) throws IOException, ParseException {
        JSONParser keyParser = new JSONParser();
        Reader reader = new FileReader(signkey);
        JSONObject jsonObject = (JSONObject) keyParser.parse(reader);

        boolean isDayOff = false;

        UriComponents uri = UriComponentsBuilder
                .newInstance()
                .scheme("http")
                .host(ApplicationConstants.API_GO_URL)
                .path(ApplicationConstants.KAI_REST_DATE_URL)
                .queryParam("solYear", targetDate.getYear())
                .queryParam("solMonth", targetDate.getMonthValue() < 10 ? "0" + targetDate.getMonthValue() : targetDate.getMonthValue())
                .queryParam("_type", "json")
                .queryParam("ServiceKey", URLDecoder.decode((String) jsonObject.get("data-go"), StandardCharsets.UTF_8))
                .queryParam("numOfRows", 10)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);

        JSONParser parser = new JSONParser();
        JSONObject object = (JSONObject) parser.parse(result.getBody());
        JSONObject response = (JSONObject) object.get("response");
        JSONObject header = (JSONObject) response.get("header");
        JSONObject body = (JSONObject) response.get("body");


        if (!body.get("items").toString().isEmpty()) {
            JSONObject items = (JSONObject) body.get("items");

            try {
                //공휴일이 1개 이상일때
                JSONArray itemList = (JSONArray) items.get("item");

                if (header.get("resultMsg").toString().equals(ApplicationConstants.REQUEST_MSG)) {
                    for (Object o : itemList) {
                        JSONObject item = (JSONObject) o;

                        if (DateUtils.toStringLocalDate(item.get("locdate").toString()).isEqual(targetDate)) {
                            isDayOff = true;
                            break;
                        }
                    }
                }
            } catch (ClassCastException e) {
                //공휴일이 1개만 내려올때
                JSONObject item = (JSONObject) items.get("item");
                if (DateUtils.toStringLocalDate(item.get("locdate").toString()).isEqual(targetDate)) {
                    isDayOff = true;
                }
            }
        }

        return isDayOff;
    }


    //주식 시세 받아오기
    @Transactional
    public void getStockPrice(String marketType, String basDt, int pageNum, int totalCount, int currentCount) throws IOException {
        Logger.debug("[DEBUG] SET STOCK DATA AT : {} {}",marketType, basDt);

        try {
            JSONParser keyParser = new JSONParser();
            Reader reader = new FileReader(signkey);
            JSONObject jsonObject = (JSONObject) keyParser.parse(reader);

            UriComponents uri = UriComponentsBuilder
                    .newInstance()
                    .scheme("http")
                    .host(ApplicationConstants.API_GO_URL)
                    .path(ApplicationConstants.KRX_STOCK_VALUE_URI)
                    .queryParam("serviceKey", URLDecoder.decode((String) jsonObject.get("data-go"), StandardCharsets.UTF_8))
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

            if (header.get("resultMsg").toString().equals(ApplicationConstants.REQUEST_MSG) && totalCount > 0) {

                if (!body.get("items").toString().isEmpty()) {
                    JSONObject items = (JSONObject) body.get("items");
                    JSONArray itemList = (JSONArray) items.get("item");

                    for (Object o : itemList) {
                        JSONObject item = (JSONObject) o;
                        StockPrice price = new StockPrice();

                        if (!item.get("itmsNm").toString().contains("스팩")) {
                            price.setStockCode(item.get("srtnCd").toString());
                            price.setMarketCode(marketType);
                            price.setBasDt(DateUtils.toStringLocalDate(basDt));
                            price.setVolume(Integer.parseInt(item.get("trqu").toString()));
                            price.setStartPrice(Integer.parseInt(item.get("mkp").toString()));
                            price.setEndPrice(Integer.parseInt(item.get("clpr").toString()));
                            price.setLowPrice(Integer.parseInt(item.get("lopr").toString()));
                            price.setHighPrice(Integer.parseInt(item.get("hipr").toString()));
                            price.setDailyRange(Double.parseDouble(item.get("vs").toString()));
                            price.setDailyRatio(Double.parseDouble(item.get("fltRt").toString()));
                            price.setStockTotalCnt(Long.parseLong(item.get("lstgStCnt").toString()));
                            price.setMarketTotalAmt(Long.parseLong(item.get("mrktTotAmt").toString()));

                            //모멘텀 세팅
                            price.setMomentum(getMomentumScore(price.getEndPrice(), price.getStockCode(), basDt));

                            //FIXME 중복 데이터 저장 방지 - 성능 개선 필요
                            if (stockPriceRepository.countByStockCodeAndBasDt(price.getStockCode(), price.getBasDt()) == 0) {
                                // primary 전략이 IDENTITY로 설정되어 중복방지를 위해 개별 저장
                                stockPriceRepository.save(price);
                            }
                        }
                        currentCount += 1;
                    }

                    if (currentCount < totalCount) {
                        getStockPrice(marketType, basDt, pageNum + 1, totalCount, currentCount + 1);
                    }
                }

            }
        } catch (ParseException e) {
            Logger.error(e);
        }
    }


    //상장 회사 고유 정보 받아오기
    @Transactional
    public void getDartCorpCodeInfo()  {
        try {
        JSONParser keyParser = new JSONParser();
        Reader reader = new FileReader(signkey);
        JSONObject jsonObject = (JSONObject) keyParser.parse(reader);


        UriComponents uri = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(ApplicationConstants.DART_API_URL)
                .path(ApplicationConstants.DART_CORP_CODE_URI)
                .queryParam("crtfc_key", jsonObject.get("dart"))
                .build();


        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_OCTET_STREAM));
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<String> entity = new HttpEntity<String>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.exchange(uri.toString(), HttpMethod.GET, entity, byte[].class);


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
                                !getValue("corp_name", corp).matches("^*\\d호.*$") &&
                                !getValue("corp_name", corp).toLowerCase(Locale.ROOT).contains("llc") &&
                                !getValue("corp_name", corp).toLowerCase(Locale.ROOT).contains("limited") &&
                                !getValue("corp_name", corp).toLowerCase(Locale.ROOT).contains("ltd") &&
                                !getValue("corp_name", corp).toLowerCase(Locale.ROOT).contains("fund")) {

                            //가격정보가 있는 데이터 인지 확인
                            if (stockPriceRepository.countByStockCode(getValue("stock_code", corp)) > 0) {
                                CorpInfo code = corpInfoRepository.findById(getValue("corp_code", corp))
                                        .orElseGet(() -> new CorpInfo().builder()
                                        .corpCode(getValue("corp_code", corp))
                                        .stockCode(getValue("stock_code", corp))
                                        .corpName(getValue("corp_name", corp))
                                        .state(CorpState.ACTIVE)
                                        .build());

                                //체크 일자 업데이트
                                code.setCheckDt(LocalDate.now());

                                if (code.getCorpName().contains("은행") || code.getCorpName().contains("금융")) {
                                    code.setCorpType(CorpType.BANK);
                                } else if (code.getCorpName().contains("지주") || code.getCorpName().contains("홀딩스")) {
                                    code.setCorpType(CorpType.HOLDING);
                                }

                                codeList.add(code);
                            }
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
            Logger.error("{}", e);
        }

    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getTextContent().trim();
    }

    // 다중회사 재무 제표 다운로드
    @Async
    public void setMultiCorpFinanceInfo(String year) {
        // 15년 이전 데이터는 지원 안함
        if (Integer.parseInt(year) < ApplicationConstants.LIMIT_YEAR) {
            return;
        }

        Long totalCount = corpInfoRepository.countByState(CorpState.ACTIVE);
        int pageSize = (int) Math.ceil(totalCount / 100.0);

        for (int i = 0; i < pageSize; i++) {
            //Dart 파라메터의 최대 parm 개수 : 100
            Pageable pageable = PageRequest.of(i, 100);
            Page<CorpCodeMapper> codePage = corpInfoRepository.findByStateAndCorpTypeIsNull(pageable, CorpState.ACTIVE);

            if (codePage.getNumberOfElements() > 0) {
                setMultiCorpFinanceInfo(codePage.getContent(), year, QuarterCode.Q1);
                setMultiCorpFinanceInfo(codePage.getContent(), year, QuarterCode.Q2);
                setMultiCorpFinanceInfo(codePage.getContent(), year, QuarterCode.Q3);
                setMultiCorpFinanceInfo(codePage.getContent(), year, QuarterCode.Q4);
            }
        }
    }

    //회사 목록 전체의 재무재표 업데이트
    @Transactional
    public void setMultiCorpFinanceInfo(List<CorpCodeMapper> infoList, String year, QuarterCode quarter) {

        if (!StringUtils.hasText(year)) {
            year = Integer.toString(LocalDate.now().getYear());
        }

        if (Integer.parseInt(year) < ApplicationConstants.LIMIT_YEAR) {
            return;
        }

        if (infoList.isEmpty()) {
            return;
        }

        StringBuffer sb = new StringBuffer();
        for (CorpCodeMapper corpCode : infoList) {
            sb.append(corpCode.getCorpCode());
            sb.append(",");
        }
        sb.setLength(sb.length() - 1);

        try {

        JSONParser keyParser = new JSONParser();
        Reader reader = new FileReader(signkey);
        JSONObject jsonObject = (JSONObject) keyParser.parse(reader);


        UriComponents uri = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(ApplicationConstants.DART_API_URL)
                .path(ApplicationConstants.DART_STOCK_FINANCE_MULTI_URI)
                .queryParam("crtfc_key", jsonObject.get("dart"))
                .queryParam("corp_code", sb)
                .queryParam("bsns_year", year)
                .queryParam("reprt_code", quarter.getCode())
                .build();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);


            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
            DartBase<FinanceItem> response = mapper.readValue(result.getBody(), DartBase.class);

            if (response.getStatus().equals("000")) {
                List<CorpFinance> financeList = new ArrayList<>();
                List<FinanceItem> financeOrigin = mapper.convertValue(response.getList(), new TypeReference<List<FinanceItem>>() {
                });

                if (!financeOrigin.isEmpty()) {
                    for (CorpCodeMapper corpCode : infoList) {
                        List<FinanceItem> financeItems = setFinanceParser(financeOrigin, corpCode.getCorpCode());
                        if (!financeItems.isEmpty()) {
                            //재무제표에 한국이 아닌 데이터 삭제
                            checkChinaStock(financeItems);

                            CorpFinance finance = setFinanceInfo(corpCode.getCorpCode(), year, quarter, financeItems);
                            if (finance.getRevenue() != null) {
                                financeList.add(finance);
                            }

                        }
                    }
                }

                financeRepository.saveAll(financeList);
            }
        } catch (Exception e) {
            Logger.error("{}", e);
        }

    }

    private void checkChinaStock(List<FinanceItem> financeItems) {
        for (Iterator<FinanceItem> iter = financeItems.iterator(); iter.hasNext(); ) {
            FinanceItem item = iter.next();
            if (item.getCurrency().equals("CNY")) {
                CorpInfo temp = corpInfoRepository.findTopByCorpCode(item.getCorp_code());
                temp.setCorpType(CorpType.CHINA);
                corpInfoRepository.save(temp);
                iter.remove();
            } else if (item.getCurrency().equals("JPY")) {
                CorpInfo temp = corpInfoRepository.findTopByCorpCode(item.getCorp_code());
                temp.setCorpType(CorpType.JAPAN);
                corpInfoRepository.save(temp);
                iter.remove();
            } else if (item.getCurrency().equals("USD")) {
                CorpInfo temp = corpInfoRepository.findTopByCorpCode(item.getCorp_code());
                temp.setCorpType(CorpType.USD);
                corpInfoRepository.save(temp);
                iter.remove();
            } else if (!item.getCurrency().equals("KRW")) {
                CorpInfo temp = corpInfoRepository.findTopByCorpCode(item.getCorp_code());
                temp.setCorpType(CorpType.ETC);
                corpInfoRepository.save(temp);
                iter.remove();
            }
        }
    }


    //단일 상장회사 재무 정보 다운로드
    @Async
    public void setSingleCorpFinanceInfo(String corpCode, String year) {
        // 15년 이전 데이터는 지원 안함
        if (Integer.parseInt(year) < ApplicationConstants.LIMIT_YEAR) {
            return;
        }

        setSingleCorpFinanceInfo(corpCode, year, QuarterCode.Q1);
        setSingleCorpFinanceInfo(corpCode, year, QuarterCode.Q2);
        setSingleCorpFinanceInfo(corpCode, year, QuarterCode.Q3);
        setSingleCorpFinanceInfo(corpCode, year, QuarterCode.Q4);
    }


    //단일 상장회사 재무 정보 다운로드
    @Transactional
    public void setSingleCorpFinanceInfo(String corpCode, String year, QuarterCode quarter) {

        // 15년 이전 데이터는 지원 안함
        if (Integer.parseInt(year) < ApplicationConstants.LIMIT_YEAR) {
            return;
        }

        try {
            JSONParser keyParser = new JSONParser();
            Reader reader = new FileReader(signkey);
            JSONObject jsonObject = (JSONObject) keyParser.parse(reader);

        UriComponents uri = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(ApplicationConstants.DART_API_URL)
                .path(ApplicationConstants.DART_STOCK_FINANCE_SINGLE_URI)
                .queryParam("crtfc_key", jsonObject.get("dart"))
                .queryParam("corp_code", corpCode)
                .queryParam("bsns_year", year)
                .queryParam("reprt_code", quarter.getCode())
                .build();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);


            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
            DartBase<FinanceItem> response = mapper.readValue(result.getBody(), DartBase.class);

            if (response.getStatus().equals("000")) {
                //같은 기간 데이터가 있는지 확인
                List<CorpFinance> financeList = new ArrayList<>();
                List<FinanceItem> financeSrcList = mapper.convertValue(response.getList(), new TypeReference<List<FinanceItem>>() {});

                //재무제표에 한국이 아닌 데이터 삭제
                checkChinaStock(financeSrcList);

                CorpFinance finance = setFinanceInfo(corpCode, year, quarter, financeSrcList);
                if (finance.getRevenue() != null) {
                    financeList.add(finance);
                }

                financeRepository.saveAll(financeList);
            }

        } catch (Exception e) {
            Logger.error("{}", e);
        }
    }


    private List<FinanceItem> setFinanceParser(List<FinanceItem> financeOriginList, String corpCode) {
        List<FinanceItem> financeList = new ArrayList<>();

        for (FinanceItem finance : financeOriginList) {
            if (finance.getCorp_code().equals(corpCode)) {
                financeList.add(finance);
            }
        }

        return financeList;
    }

    private CorpFinance setFinanceInfo(String corpCode, String year, QuarterCode quarter, List<FinanceItem> financeSrcList) throws RuntimeException {

        CorpFinance finance = new CorpFinance();

        finance.setReprtCode(financeSrcList.get(0).getReprt_code());
        finance.setRceptNo(financeSrcList.get(0).getRcept_no());
        finance.setYearCode(year);
        finance.setStockCode(financeSrcList.get(0).getStock_code());
        finance.setCorpCode(financeSrcList.get(0).getCorp_code());
        finance.setCurrency(financeSrcList.get(0).getCurrency());

        finance.setStartDt(DateUtils.toStringLocalDate(financeSrcList.get(0).getFrmtrm_dt().replace(" 현재", "")));
        finance.setEndDt(DateUtils.toStringLocalDate(financeSrcList.get(0).getThstrm_dt().replace(" 현재", "")));


        for (FinanceItem item : financeSrcList) {
            if (item.getFs_div().equalsIgnoreCase("OFS")) {
                Long value;

                if (StringUtils.hasText(item.getThstrm_amount())) {
                    //음수 파싱 중 문제가 발생하는 경우가 있어 음수 체크
                    if (item.getThstrm_amount().startsWith("-")) {
                        value = Long.parseLong(item.getThstrm_amount().replaceFirst("-", "").replaceAll(",", ""));
                        value = value * -1L;
                    } else {
                        value = Long.parseLong(item.getThstrm_amount().replaceAll(",", ""));
                    }

                    if (item.getSj_div().equalsIgnoreCase("IS")) {
                        if (item.getAccount_nm().equals("매출액")) {
                            finance.setRevenue(value);

                        } else if (item.getAccount_nm().equals("영업이익")) {
                            finance.setOperatingProfit(value);
                        } else if (item.getAccount_nm().equals("당기순이익")) {
                            finance.setNetIncome(value);
                        }
                    } else if (item.getSj_div().equalsIgnoreCase("BS")) {
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
                }
            }
        }

        //YOY, QoQ 계산
        setFinanceRatio(corpCode, year, quarter, finance);
        //PSR PER 계산
        setFinanceIndicators(finance);

        return finance;
    }

    private void setFinanceRatio(String corpCode, String year, QuarterCode quarter, CorpFinance finance) {
        //분기 데이터의 마지막일자 시가총액 불러오기
        PriceMapper nowPrice = stockPriceRepository.findTopByStockCodeAndBasDtBetweenOrderByBasDtDesc(finance.getStockCode(), finance.getEndDt().minusDays(5),finance.getEndDt());

        //전년도 재무정보
        CorpFinance byFinance = financeRepository.findByCorpCodeAndRceptNoAndYearCode(corpCode, quarter.getCode(), String.valueOf(Integer.parseInt(year) - 1));

        if (byFinance != null) {
            Double yoy = ((finance.getRevenue().doubleValue() - byFinance.getRevenue().doubleValue()) / nowPrice.getMarketTotalAmt()) * 100;
            finance.setYOY(yoy);
        }

        //전분기 재무정보 가지고 오기
        CorpFinance bqFinance;
        if (quarter.equals(QuarterCode.Q1)) {
            //전분기 재무정보 (1분기 값은 작년정보)
            bqFinance = financeRepository.findByCorpCodeAndRceptNoAndYearCode(corpCode, quarter.getBefore(), String.valueOf(Integer.parseInt(year) - 1));
        } else {
            //전분기 재무정보
            bqFinance = financeRepository.findByCorpCodeAndRceptNoAndYearCode(corpCode, quarter.getBefore(), year);
        }
        setFinanceGrowth(bqFinance, finance, nowPrice);
    }

    private void setFinanceGrowth(CorpFinance bqFinance, CorpFinance finance, PriceMapper nowPrice) {
        if (bqFinance != null && nowPrice != null) {
            Double qoq = ((finance.getRevenue().doubleValue() - bqFinance.getRevenue().doubleValue()) / nowPrice.getMarketTotalAmt()) * 100;
            finance.setQOQ(qoq);

            Double OPGE = ((finance.getOperatingProfit().doubleValue() - bqFinance.getOperatingProfit().doubleValue()) / nowPrice.getMarketTotalAmt()) * 100;
            finance.setOPGE(OPGE);

            Double PGE = ((finance.getNetIncome().doubleValue() - bqFinance.getNetIncome().doubleValue()) / nowPrice.getMarketTotalAmt()) * 100;
            finance.setPGE(PGE);
        }
    }

    public void setFinanceIndicators(CorpFinance finance) {
        //분기 데이터의 마지막일자 시가총액 불러오기
        PriceMapper nowPrice = stockPriceRepository.findTopByStockCodeAndBasDtBetweenOrderByBasDtDesc(finance.getStockCode(), finance.getEndDt().minusDays(5),finance.getEndDt());

        if (nowPrice != null) {
            finance.setPSR(nowPrice.getMarketTotalAmt().doubleValue() / finance.getRevenue().doubleValue());
            finance.setPBR(nowPrice.getMarketTotalAmt().doubleValue() / finance.getTotalEquity().doubleValue());
            finance.setPER(nowPrice.getMarketTotalAmt().doubleValue() / finance.getNetIncome().doubleValue());
            finance.setPOR(nowPrice.getMarketTotalAmt().doubleValue() / finance.getOperatingProfit().doubleValue());
        }
    }


    //주식의 가격 평균 배치
    //@Async
    public void setStockPriceAverage(LocalDate targetDate) {
        if (targetDate == null) {
            targetDate = LocalDate.now();
        }

        List<CorpCodeMapper> targetCorp = corpInfoRepository.findByState(CorpState.ACTIVE);

        for (CorpCodeMapper corp : targetCorp) {
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

    Integer getMomentumScore(Integer price, String code, String basDt) {
        LocalDate baseDate = DateUtils.toStringLocalDate(basDt);
        Integer score = 0;

        //모멘텀은 전달 데이터를 가지고 온다.
        for (int i = 1; i <= 12; i++) {
            LocalDate target = baseDate.minusMonths(i);
            if (target.getDayOfWeek().getValue() == 6) {
                target = target.minusDays(1);
            } else if (target.getDayOfWeek().getValue() == 0) {
                target = target.plusDays(1);
            }

            //타켓일자의 값이 없을수 있어 5일전 데이터 까지 불러 최근날짜 데이터를 가지고 옴
            PriceMapper bfPrice = stockPriceRepository.findTopByStockCodeAndBasDtBetweenOrderByBasDtDesc(code, target.minusDays(5),target);

            if (bfPrice != null) {
                if (price > bfPrice.getEndPrice()) {
                    score++;
                } else if (price < bfPrice.getEndPrice()) {
                    score--;
                }
            }
        }

        return score;
    }

    public List<RecommendDto> getStockRecommend(LocalDate date, String portKey) {

        Portfolio portfolio = portfolioRepository.findByPortfolioId(portKey);
        if (portfolio == null) {
            throw new InvalidRequestException("일치하는 포트폴리오 없음");
        }

        String[] indicator = portfolio.getIndicator().split(ApplicationConstants.SPLIT_KEY);

        if (indicator.length == 0) {
            throw new InvalidRequestException("포트폴리오 조건이 없음");
        }

        List<StockOrder> orderList = new ArrayList<>();

        //선별된 주식리스트에 가중치를 부여
        for (String key : indicator) {
            List<StockDto> list = financeSupport.findByStockOrderSet(date, portfolio.getMarket(), key, portfolio.getRanges(), portfolio.getStockCount(), portfolio.getMomentumScore());
            getStockOrderList(orderList, list);
        }

        // 내림차순 정렬
        orderList.sort(Collections.reverseOrder());

        //가중치 추가를 하면서 개수가 많아졌다면 자르기
        if (orderList.size() > portfolio.getStockCount()) {
            orderList = orderList.subList(0, portfolio.getStockCount() - 1);
        }


        //현재 가격, 포트폴리오 상 자본금을 고려하여 구매 개수 설정
        List<RecommendDto> result = new ArrayList<>();
        int payForStock = portfolio.getTotalValue() / portfolio.getStockCount();
        for (StockOrder temp : orderList) {

            RecommendDto recommend = RecommendDto.builder()
                    .stockCode(temp.getStock().getStockCode())
                    .corpName(temp.getStock().getCorpName())
                    .price(temp.getStock().getEndPrice())
                    .count(getBuyStockCount(temp, payForStock, portfolio.getRatioYn(), date) )
                    .build();


            //트레이딩 기록 추가
            setTradeInfo(portfolio.getUserInfo().getUserKey(), recommend.getStockCode(), recommend.getCount() , recommend.getPrice());
            result.add(recommend);

        }


        return result;
    }

    //주식 구매 개수 계산
    private Integer getBuyStockCount(StockOrder target, int pay, Character ratioYn, LocalDate date){
        int stockCount = pay / target.getStock().getEndPrice();

        if(ratioYn.equals('Y') ){
            List<StockAverage> averageList = stockAverageRepository.findByStockCodeAndTarDt(target.getStock().getStockCode(), date);

            if(averageList != null && !averageList.isEmpty()){
                int upperCount = averageList.size();

                //해당 주식의 종가가 평균가보다 작은 경우 구매 개수 삭감
                for(StockAverage average : averageList){
                    if(target.getStock().getEndPrice() < average.getPrice()){
                        upperCount --;
                    }
                }
                stockCount = stockCount * (upperCount/averageList.size());
            }
        }

        return stockCount;
    }

    //주식 로그 추가
    private void setTradeInfo(String userKey, String stockCode, Integer stockCount, Integer price){
        Trade trade = tradeRepository.findByUserKeyAndStockCode(userKey, stockCode)
                .orElseGet(() -> Trade.builder()
                        .userKey(userKey)
                        .tradingDt(LocalDate.now())
                        .stockCode(stockCode)
                        .stockCount(0)
                        .totalAsset(0)
                        .average((double)0)
                        .build()
                );
        trade.setStockCount(trade.getStockCount() + stockCount );
        trade.setTotalAsset(trade.getTotalAsset() + (stockCount * price));
        trade.setAverage(Math.floor(((double) trade.getTotalAsset() / trade.getStockCount()) * 100)/100.0 );
    }



    public List<RecommendDto> getStockRecommendOne(LocalDate date, String market, Integer value, Integer count, AmtRange range, Character ratioYn,  List<String> indicator , Integer momentum ) {

        if (indicator.isEmpty()) {
            throw new InvalidRequestException("포트폴리오 조건이 없음");
        }

        List<StockOrder> orderList = new ArrayList<>();

        //선별된 주식리스트에 가중치를 부여
        for (String key : indicator) {
            List<StockDto> list = financeSupport.findByStockOrderSet(date, market, key, range, count, momentum);

            getStockOrderList(orderList, list);
        }

        // 내림차순 정렬
        orderList.sort(Collections.reverseOrder());

        //가중치 추가를 하면서 개수가 많아졌다면 자르기
        if (orderList.size() > count) {
            orderList = orderList.subList(0, count - 1);
        }

        //현재 가격, 포트폴리오 상 자본금을 고려하여 구매 개수 설정
        List<RecommendDto> result = new ArrayList<>();
        int payForStock = value / count;
        for (StockOrder temp : orderList) {

            result.add(RecommendDto.builder()
                    .stockCode(temp.getStock().getStockCode())
                    .corpName(temp.getStock().getCorpName())
                    .price(temp.getStock().getEndPrice())
                    .count(getBuyStockCount(temp, payForStock, ratioYn, date ))
                    .build());
        }

        return result;
    }

    private void getStockOrderList(List<StockOrder> orderList, List<StockDto> list) {
        for (int i = 0; i < list.size(); i++) {
            StockDto item = list.get(i);
            boolean check = false;

            for (StockOrder order : orderList) {
                if (item.getStockCode().equals(order.getStock().getStockCode())) {
                    //가중치를 추가로 부여 한다.
                    order.setOrder(order.getOrder() + list.size() - i);
                    //리스트에 데이터가 있음을 체크
                    check = true;
                    break;
                }
            }

            //리스트 내부에 아이템이 없다면 추가
            if (!check) {
                orderList.add(StockOrder.builder()
                        .stock(item)
                        .order(list.size() - i)
                        .build());
            }
        }
    }
}
