package com.stock.quant.service.Util;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class DateUtils {


    public static String getStringNowDateFormat(String pattern) {
        return getStringDateFormat(LocalDateTime.now(),pattern);
    }

    public static String getStringDateFormat(LocalDateTime ldt , String pattern){
        return ldt.format(DateTimeFormatter.ofPattern(pattern).withLocale(Locale.KOREAN));
    }


    public static LocalDateTime toStringLocalDateTime(String str){
        LocalDate date = LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return date.atStartOfDay();
    }


    public static LocalDate toStringLocalDate(String str){
        LocalDate date = LocalDate.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return date;
    }

    public static String toLocalDatetimeString(LocalDateTime dateTime){
        String formatDate = "";

        if(dateTime != null){
            formatDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        return formatDate;
    }

}

