package com.quant.core.entity;

import com.quant.core.enums.AmtRange;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_STOCK_PORTFOLIO")
public class Portfolio {

    @Id
    @GeneratedValue(generator = "key-generator")
    @GenericGenerator(name = "key-generator",
            parameters = @org.hibernate.annotations.Parameter(name = "prefix", value = "PF"),
            strategy = "com.quant.core.config.KeyGenerator")
    String portId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_key")
    @ToString.Exclude
    UserInfo userInfo;

    //포트폴리오 투자금
    Long totalValue;

    //최소 모멘텀
    Integer momentumScore;

    //포트폴리오당 최대 종목개수
    Integer stockCount;

    String market;

    //손절 퍼센트
    Integer lossCut;

    //시총 범위
    @Enumerated(EnumType.STRING)
    AmtRange ranges;

    //지표 (ex per,psr ...)
    String indicator;

    //리벨런싱 (ex 1,6,12 ...)
    String rebalance;

    //구매 비율 적용
    //현재 가격보다 아래에 있는 이동평균의 개수에 따라 구매 개수 퍼센트 적용
    Character ratioYn;

    //기타 설명 문구
    String comment;

}
