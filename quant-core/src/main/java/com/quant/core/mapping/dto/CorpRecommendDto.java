package com.quant.core.mapping.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class CorpRecommendDto implements Serializable {
    String corpCode;

    String corpName;

    String stockCode;

    //매수 개수
    String count;
}
