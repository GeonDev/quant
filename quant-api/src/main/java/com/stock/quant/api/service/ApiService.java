package com.stock.quant.api.service;

import com.stock.quant.api.consts.ApplicationConstants;
import com.stock.quant.api.model.dataGo.StockDateItem;
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
                .queryParam("ServiceKey",apiKey)
                .queryParam("solYear", date.getYear())
                .queryParam("solMonth", date.getMonth())
                .queryParam("_type", "json")
                .encode(Charset.defaultCharset())
                .build()
                .toUri();

        RestTemplate restTemplate = new RestTemplate();

        DataResponse<StockDateItem> response = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<DataResponse<StockDateItem>>(){}).getBody();

        boolean isDayOff = false;

        for(StockDateItem item : response.getBody().getItems()){
            if(DateUtils.toStringLocalDate(item.getLocdate()).isEqual(date)){
                isDayOff = true;
                break;
            }
        }

        if(!isDayOff){





        }


    }

}
