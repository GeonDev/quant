package com.quant.core.entity;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "TB_CORP_FINANCE")
public class CorpFinance implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    Long financeId;

    //분기 코드
    String reprtCode;

    //재무 재표 코드
    String rceptNo;

    String corpCode;

    String stockCode;

    //연도 4자리
    String yearCode;

    LocalDate startDt;

    LocalDate endDt;

    //자본금
    Long capital;

    //자산 총계
    Long totalAssets;

    //부채 총계
    Long totalDebt;

    //자본 총계
    Long totalEquity;

    //매출액
    Long revenue;

    //당기 순이익
    Long netIncome;

    //영업 이익
    Long operatingProfit;

    //이익 잉여금
    Long earnedSurplus;

    //시가총액 % 매출액
    Double PSR;

    //시가총액 % 자본 총계
    Double PBR;

    //시가총액 % 당기 순이익
    Double PER;

    //시가총액 % 영업이익
    Double POR;

    //전년도 매출액 성장율 (시가총액 반영)
    Double YOY;

    //분기 매출액 성장율 (시가총액 반영)
    Double QOQ;

    //영업 이익 성장율
    Double OPGE;

    //주가 순이익 성장율
    Double PGE;

}
