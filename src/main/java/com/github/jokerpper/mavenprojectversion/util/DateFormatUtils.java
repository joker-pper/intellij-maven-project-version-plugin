package com.github.jokerpper.mavenprojectversion.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatUtils {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

    public static String formatDateTime(Date date) {
        return formatDateTime(date, DEFAULT_PATTERN);
    }

    public static String formatDateTime(Date date, String pattern) {
        try {
            ZoneId zoneId = ZoneId.systemDefault();
            return DateTimeFormatter.ofPattern(pattern).format(LocalDateTime.ofInstant(date.toInstant(), zoneId));
        } catch (Throwable throwable) {
            //ignore
        }
        DateFormat dateFormat = new SimpleDateFormat(pattern);
        return dateFormat.format(date);
    }

}
