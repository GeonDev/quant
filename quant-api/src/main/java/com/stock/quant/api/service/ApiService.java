package com.stock.quant.api.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.quant.api.consts.ApplicationConstants;
import com.stock.quant.api.model.dart.DartBase;
import com.stock.quant.api.model.dart.FinanceItem;
import com.stock.quant.api.model.dataGo.StockDateItem;
import com.stock.quant.api.model.dataGo.StockPriceItem;
import com.stock.quant.api.model.dataGo.base.ApiResponse;
import com.stock.quant.api.model.enums.StockType;
import utils.CommonUtils;
import utils.DateUtils;
import com.stock.quant.service.entity.CorpCode;
import com.stock.quant.service.repository.CorpCodeRepository;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.URLDecoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class ApiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CorpCodeRepository corpCodeRepository;


    @Value("${signkey.data-go}")
    String apiKey;

    @Value("${signkey.dart}")
    String dartKey;

    @Value("${file.path}")
    String filePath;


    //주식 시장 활성일 체크 -> 활성일 일 경우 주식 시세 받기
    public void getKrxDailyInfo() {
        LocalDate targetDate = LocalDate.now();

        //공공정보 API는 1일전 데이터가 최신, 전일 데이터는 오후 1시에 갱신, 월요일에 금요일 데이터 갱신
        if(targetDate.getDayOfWeek().getValue() == 1){
            targetDate = targetDate.minusDays(3);
        }else {
            targetDate = targetDate.minusDays(1);
        }

        try {
            UriComponents uri = UriComponentsBuilder
                    .newInstance()
                    .scheme("http")
                    .host(ApplicationConstants.API_GO_URL)
                    .path(ApplicationConstants.KAI_REST_DATE_URL)
                    .queryParam("solYear", targetDate.getYear())
                    .queryParam("solMonth", targetDate.getMonthValue())
                    .queryParam("_type", "json")
                    .queryParam("ServiceKey", URLDecoder.decode(apiKey, "UTF-8") )
                    .queryParam("numOfRows", 20)
                    .build();

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);


            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
            ApiResponse<StockDateItem> response = mapper.readValue(result.getBody(), ApiResponse.class);

            boolean isDayOff = false;
            if(response.getResponse().getBody().getItems() != null ){
                for (StockDateItem item : response.getResponse().getBody().getItems().getItem()) {
                    if (DateUtils.toStringLocalDate(item.getLocdate()).isEqual(targetDate)) {
                        isDayOff = true;
                        break;
                    }
                }
            }

            if (!isDayOff) {
                List<StockPriceItem> kospiPriceList = new ArrayList<>();
                getStockPrice(kospiPriceList, StockType.KOSPI.name(), DateUtils.toLocalDateString(targetDate),1, 0);

                List<StockPriceItem> kodaqPriceList = new ArrayList<>();
                getStockPrice(kodaqPriceList, StockType.KOSDAQ.name(), DateUtils.toLocalDateString(targetDate),1, 0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    //주식 시세 받아오기
    public void getStockPrice(List<StockPriceItem> stockPriceList, String marketType, String basDt, int pageNum, int totalCount) {

        if (totalCount != 0 && totalCount == stockPriceList.size()) {
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

            if (!result.getBody().contains(ApplicationConstants.REQUEST_MSG)) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);

                ApiResponse<StockPriceItem> response = mapper.readValue(result.getBody(), ApiResponse.class);

                if(response.getResponse().getBody().getItems() != null){
                    for (StockPriceItem item : response.getResponse().getBody().getItems().getItem()) {
                        stockPriceList.add(item);
                    }

                    getStockPrice(stockPriceList, marketType, basDt, pageNum + 1, Integer.parseInt(response.getResponse().getBody().getTotalCount()));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //상장 회사 고유 정보 받아오기
    public void getDartCorpCodeInfo(){
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
            File lOutFile = new File(filePath  + "temp.zip");
            FileOutputStream lFileOutputStream = new FileOutputStream(lOutFile);
            lFileOutputStream.write(response.getBody());
            lFileOutputStream.close();

            CommonUtils.unZip(filePath + "temp.zip" , filePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();

            Document document = builder.parse( filePath + "CORPCODE.xml");

            NodeList corpList = document.getElementsByTagName("list");

            List<CorpCode> codeList = new ArrayList<>();

            for(int i =0; i< corpList.getLength(); i++ ){
                Element corp = (Element) corpList.item(i);
                if(corp != null){
                    CorpCode code = new CorpCode();
                    code.setCorpCode(getValue("corp_code" , corp));
                    code.setStockCode(getValue("stock_code" , corp));
                    code.setCorpName(getValue("corp_name" , corp));
                    codeList.add(code);
                }
            }

            corpCodeRepository.saveAll(codeList);

        } catch (Exception e){
            e.printStackTrace();
        }

    }

    private static String getValue(String tag, Element element) {
        NodeList nodes = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodes.item(0);
        return node.getTextContent().trim();
    }


    // 살장회사 재무 정보 다운로드
    public void getCorpFinanceInfo(String corpCode, String year, String reprtCode){
        UriComponents uri = UriComponentsBuilder
                .newInstance()
                .scheme("https")
                .host(ApplicationConstants.DART_API_URL)
                .path(ApplicationConstants.DART_STOCK_FINANCE_URI)
                .queryParam("crtfc_key", dartKey)
                .queryParam("corp_code", corpCode)
                .queryParam("bsns_year", year)
                .queryParam("reprt_code", reprtCode)
                .build();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(uri.toString(), String.class);

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
            mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
            DartBase<FinanceItem> response = mapper.readValue(result.getBody(), DartBase.class);

            if(response.getStatus().equals("000") ){
                List<FinanceItem> financeList = mapper.convertValue(response.getList(), new TypeReference<List<FinanceItem>>() {});
                for(FinanceItem item : financeList){
                    logger.debug(item.toString());
                }
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }




}
