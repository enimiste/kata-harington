package com.harington.kata.bank.formatters;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public final class DatesFormatter {
    private final static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final static DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public static String format(LocalDateTime dateTime){
        if(dateTime==null) return "";
        return dateTime.format(DATE_TIME_FORMAT);
    }

    public static String formatOnlyDate(LocalDateTime dateTime){
        if(dateTime==null) return "";
        return dateTime.toLocalDate().format(DATE_FORMAT);
    }
}
