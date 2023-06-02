package com.quant.stock.entity;

import com.quant.stock.model.enums.CorpOption;
import com.quant.stock.model.enums.CorpState;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;


@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_CORP_CODE")
public class CorpCode implements Serializable {


    @Id
    String corpCode;

    String corpName;

    String stockCode;

    @Enumerated(EnumType.STRING)
    CorpState state;

    @Enumerated(EnumType.STRING)
    CorpOption option;

    String message;

    LocalDate checkDt;

}
