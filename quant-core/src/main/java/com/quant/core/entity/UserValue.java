package com.quant.core.entity;

import com.quant.core.enums.TradingType;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table(name = "TB_USER_VALUE_HISTORY")
public class UserValue implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long valueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_key")
    @ToString.Exclude
    UserInfo userInfo;

    //잔고(예수금 + 평가 금액)
    Integer balance;

    //예수금
    Integer deposit;

    //평가 금액
    Integer marketValue;

    @Enumerated(EnumType.STRING)
    TradingType tradeType;

    //변동 사항
    String comment;

    @CreatedDate
    LocalDate regDt;

}
