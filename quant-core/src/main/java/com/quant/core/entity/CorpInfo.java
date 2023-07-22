package com.quant.core.entity;

import com.quant.core.enums.CorpState;
import com.quant.core.enums.CorpType;
import com.quant.core.enums.IncomeState;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_CORP_INFO")
public class CorpInfo implements Serializable {

    @Id
    String corpCode;

    String corpName;

    String stockCode;

    @Enumerated(EnumType.STRING)
    CorpState state;

    @Enumerated(EnumType.STRING)
    IncomeState income;

    @Enumerated(EnumType.STRING)
    CorpType corpType;

    //기타 메모
    String message;

    //검사일 -> 상장 폐지인지 확인
    LocalDate checkDt;

}
