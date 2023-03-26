package com.stock.quant.service.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Data
@Table(name = "TB_CORP_CORE")
public class CorpCode {

    @Id
    String corpCode;

    String corpName;

    String stockCode;

}
