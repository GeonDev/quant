package com.quant.core.entity.subSelect;

import lombok.Getter;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Subselect(
        "SELECT x.RCEPT_NO, x.CORP_CODE, x.REVENUE, x.PSR, x.PBR, x.PER, x.POR, x.YOY, x.QOQ, x.OPGE, x.PGE " +
                "FROM TB_CORP_FINANCE x " +
                "WHERE x.END_DT = (SELECT MAX(t.END_DT) FROM TB_CORP_FINANCE t WHERE t.CORP_CODE = x.CORP_CODE)"
)
@Immutable
public class subFinance {

    @Id
    String rceptNo;

    String corpCode;

    //매출액
    Long revenue;

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
