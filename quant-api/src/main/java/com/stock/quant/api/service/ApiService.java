package com.stock.quant.api.service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.quant.api.consts.ApplicationConstants;
import com.stock.quant.api.model.dataGo.StockDateItem;
import com.stock.quant.api.model.dataGo.StockPriceItem;
import com.stock.quant.api.model.dataGo.base.DataResponse;
import com.stock.quant.service.Util.DateUtils;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;

import org.apache.juli.logging.LogFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiService {

    @Value("${signkey.data-go}")
    String apiKey;

    public void getKrxDailyInfo() {
        LocalDate date = LocalDate.now();

        URI uri = UriComponentsBuilder
                .fromUriString(ApplicationConstants.API_GO_URL)
                .path(ApplicationConstants.KAI_REST_DATE_URL)
                .queryParam("serviceKey",apiKey)
                .queryParam("solYear", date.getYear())
                .queryParam("solMonth", date.getMonth())
                .queryParam("_type", "json")
                .encode(Charset.defaultCharset())
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> result = restTemplate.getForEntity(uri,String.class);

        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        System.out.println(result.getBody());

        //BoardBanUserInfo temp = mapper.readValue(result.getBody(), BoardBanUserInfo.class);

        try {
            DataResponse<StockDateItem> response = mapper.readValue(result.getBody(), DataResponse.class);
            boolean isDayOff = false;
            for(StockDateItem item : response.getBody().getItems().getItem()){
                if(DateUtils.toStringLocalDate(item.getLocdate()).isEqual(date)){
                    isDayOff = true;
                    break;
                }
            }

            if(!isDayOff){
                //공공정보 API는 3일전 데이터가 최신
                String basDt = DateUtils.getStringDateFormat(LocalDateTime.now().minusDays(3),"yyyyMMdd");

                List<StockPriceItem> kospiPriceList = new ArrayList<>();
                //getStockPrice(kospiPriceList, StockType.KOSPI.name(), basDt,1, 0);

                List<StockPriceItem> kodaqPriceList = new ArrayList<>();
                //getStockPrice(kodaqPriceList, StockType.KOSDAQ.name(), basDt,1, 0);
            }

        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /**
        DataResponse<StockDateItem> response = restTemplate.exchange(uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<DataResponse<StockDateItem>>(){})
                .getBody();


        **/


    }


    private void getStockPrice(List<StockPriceItem> stockPriceList, String marketType, String basDt, int pageNum, int totalCount){

        if(totalCount != 0 && totalCount == stockPriceList.size()){
            return;
        }

        URI uri = UriComponentsBuilder.fromUriString(ApplicationConstants.API_GO_URL)
                .path(ApplicationConstants.KRX_STOCK_VALUE_URI)
                .queryParam("serviceKey",apiKey)
                .queryParam("numOfRows",ApplicationConstants.PAGE_SIZE)
                .queryParam("pageNo",pageNum)
                .queryParam("resultType","json")
                .queryParam("mrktCls" ,marketType)
                .queryParam("basDt", basDt)
                .encode(Charset.defaultCharset())
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();
        DataResponse<StockPriceItem> response = restTemplate.exchange(uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<DataResponse<StockPriceItem>>(){})
                .getBody();


        for(StockPriceItem item : response.getBody().getItems().getItem() ){
            log.debug("[DEBUG] Stock item : {}", item.toString());
            stockPriceList.add(item);
        }

        getStockPrice(stockPriceList, marketType, basDt,pageNum + 1, Integer.parseInt(response.getBody().getTotalCount()));

    }

}
