package com.quant.core.utils;

import com.quant.core.exception.InvalidRequestException;
import org.springframework.util.StringUtils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateUtils {

    public static String getStringNowDateFormat(String pattern) {
        return getStringDateFormat(LocalDateTime.now(),pattern);
    }

    public static String getStringDateFormat(LocalDateTime ldt , String pattern){
        return ldt.format(DateTimeFormatter.ofPattern(pattern).withLocale(Locale.KOREAN));
    }

    public static LocalDate toStringLocalDate(String str){
        if(StringUtils.hasText(str)){
            if(str.matches("^[\\d]{4}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$") ){
                return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }else if(str.matches("^[\\d]{4}\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01])$") ){
                return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy.MM.dd"));
            }else if(str.matches("^[\\d]{4}(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01])$")){
                return LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyyMMdd"));
            }
        }

        throw new IllegalArgumentException();
    }

    public static String toLocalDatetimeString(LocalDateTime dateTime){
        String formatDate = "";
        if(dateTime != null){
            formatDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
        return formatDate;
    }

    public static String toLocalDateString(LocalDate date){
        String formatDate = "";
        if(date != null){
            formatDate = date.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        return formatDate;
    }

}

