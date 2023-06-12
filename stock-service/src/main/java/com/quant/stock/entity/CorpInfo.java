package com.quant.stock.entity;

import com.quant.stock.model.enums.CorpType;
import com.quant.stock.model.enums.CorpState;
import com.quant.stock.model.enums.IncomeState;
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

    String message;

    LocalDate checkDt;

}
