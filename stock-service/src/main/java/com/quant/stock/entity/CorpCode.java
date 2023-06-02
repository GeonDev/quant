package com.quant.stock.entity;

import com.quant.stock.model.enums.corpOption;
import com.quant.stock.model.enums.corpState;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;


@Entity
@Data
@Table(name = "TB_CORP_CODE")
public class CorpCode {

    @Id
    String corpCode;

    String corpName;

    String stockCode;

    @Enumerated(EnumType.STRING)
    corpState state;

    @Enumerated(EnumType.STRING)
    corpOption option;

    String message;

    LocalDate checkDt;

}
