package com.stock.quant.api.service;

import com.stock.quant.api.consts.ApplicationConstants;
import com.stock.quant.api.model.dataGo.StockDateItem;
import com.stock.quant.api.model.dataGo.StockPriceItem;
import com.stock.quant.api.model.dataGo.base.DataResponse;
import com.stock.quant.service.Util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiService {

    @Value("${signkey.data-go}")
    String apiKey;


    public void getKrxDailyInfo(){
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

        DataResponse<StockDateItem> response = restTemplate.exchange(uri,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<DataResponse<StockDateItem>>(){})
                .getBody();

        boolean isDayOff = false;

        for(StockDateItem item : response.getBody().getItems().getItem()){
            if(DateUtils.toStringLocalDate(item.getLocdate()).isEqual(date)){
                isDayOff = true;
                break;
            }
        }


        if(!isDayOff){
            String basDt = DateUtils.getStringNowDateFormat("yyyyMMdd");

            List<StockPriceItem> kospiPriceList = new ArrayList<>();
            getStockPrice(kospiPriceList, "KOSPI", basDt,1, 0);

            List<StockPriceItem> kodaqPriceList = new ArrayList<>();
            getStockPrice(kodaqPriceList, "KOSDAQ", basDt,1, 0);
        }
    }


    public void getStockPrice(List<StockPriceItem> stockPriceList, String marketType, String basDt, int pageNum, int totalCount){

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
