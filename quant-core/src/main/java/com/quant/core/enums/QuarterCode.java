package com.quant.core.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum QuarterCode {

    Q1("11013"),
    Q2("11012"),
    Q3("11014"),
    Q4("11011");

    private String code;


    public String getBefore(){
        switch (this.code){
            case "11013":
                return "11011";
            case "11012":
                return "11013";
            case "11014":
                return "11012";
            case "11011":
                return "11014";
        }

        return "";
    }

}
