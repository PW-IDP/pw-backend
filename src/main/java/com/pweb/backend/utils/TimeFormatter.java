package com.pweb.backend.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TimeFormatter {
    public static Date StringToDate() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        formatter.setTimeZone(TimeZone.getTimeZone("Europe/Bucharest"));
        return formatter.parse(formatter.format(date));
    }
}
