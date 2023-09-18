package com.quant.core.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
public class RecommendDto implements Serializable {

    String corpName;

    //주식 코드
    String stockCode;

    //주식 가격 (종가)
    Integer price;

    //매수 개수
    Integer count;

}
