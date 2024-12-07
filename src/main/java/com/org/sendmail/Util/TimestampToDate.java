package com.org.sendmail.Util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class TimestampToDate {

    public static String toTime(long timestamp){
        // 使用 Instant 类表示秒级时间戳
        Instant instant = Instant.ofEpochSecond(timestamp);
        // 将 Instant 转换为系统时区的 LocalDateTime
        LocalDateTime dateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
        // 格式化日期
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        // 输出格式化后的日期
        return dateTime.format(formatter);
    }
}
