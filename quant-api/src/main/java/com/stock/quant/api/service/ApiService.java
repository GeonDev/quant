package com.stock.quant.api.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stock.quant.api.consts.ApplicationConstants;
import com.stock.quant.api.model.dataGo.StockDateItem;
import com.stock.quant.api.model.dataGo.StockPriceItem;
import com.stock.quant.api.model.dataGo.base.ApiResponse;
import com.stock.quant.api.model.dataGo.base.DataResponse;
import com.stock.quant.api.model.enums.StockType;
import com.stock.quant.service.Util.DateUtils;
import lombok.RequiredArgsConstructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ApiService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${signkey.data-go}")
    String apiKey;

    public void getKrxDailyInfo() {
        LocalDate targetDate = LocalDate.now();

        //공공정보 API는 1일전 데이터가 최신, 전일 데이터는 오후 1시에 갱신, 월요일에 금요일 데이터 갱신
        if(targetDate.getDayOfWeek().getValue() == 1){
            targetDate.minusDays(3);
        }else {
            targetDate.minusDays(1);
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

            logger.debug("API : {}" ,result.getBody());

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
                //getStockPrice(kodaqPriceList, StockType.KOSDAQ.name(), basDt,1, 0);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void getStockPrice(List<StockPriceItem> stockPriceList, String marketType, String basDt, int pageNum, int totalCount) {

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
            logger.debug("Stock API : {} ", result.getBody() );

            if (!result.getBody().contains(ApplicationConstants.REQUEST_MSG)) {
                ObjectMapper mapper = new ObjectMapper();
                mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);

                ApiResponse<StockPriceItem> response = mapper.readValue(result.getBody(), ApiResponse.class);

                if(response.getResponse().getBody().getItems() != null){
                    for (StockPriceItem item : response.getResponse().getBody().getItems().getItem()) {
                        logger.debug("Stock item : {}", item.toString());
                        stockPriceList.add(item);
                    }
                }

                getStockPrice(stockPriceList, marketType, basDt, pageNum + 1, Integer.parseInt(response.getResponse().getBody().getTotalCount()));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
