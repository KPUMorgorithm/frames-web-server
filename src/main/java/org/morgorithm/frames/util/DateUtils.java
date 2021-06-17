package org.morgorithm.frames.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateUtils {
    public static Date locaDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault());
    }
}
