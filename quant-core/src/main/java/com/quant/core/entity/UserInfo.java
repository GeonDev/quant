package com.quant.core.entity;

import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


import javax.persistence.*;
import java.util.List;

@Entity
@Builder
@Data
@Table(name = "TB_USER_INFO")
public class UserInfo {

    @Id
    @GeneratedValue(generator = "key-generator")
    @GenericGenerator(name = "key-generator",
            parameters = @Parameter(name = "prefix", value = "UR"),
            strategy = "com.quant.core.config.KeyGenerator")
    String userKey;

    String email;

    @OneToMany(mappedBy = "userInfo")
    List<Portfolio> portfolios;

}
