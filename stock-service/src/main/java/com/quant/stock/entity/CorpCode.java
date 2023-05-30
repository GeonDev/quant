package com.quant.stock.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Data
@Table(name = "TB_CORP_CODE")
public class CorpCode {

    @Id
    String corpCode;

    String corpName;

    String stockCode;

    String corpState;

}
