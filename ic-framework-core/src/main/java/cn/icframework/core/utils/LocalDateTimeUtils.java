package cn.icframework.core.utils;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;


/**
 * LocalDateTime 工具类，提供日期时间相关的常用操作方法。
 * 所有方法均为静态方法。
 */
public class LocalDateTimeUtils {
    public final static String TIME_CHINESE_FORMAT_6STR = "yyyy年MM月dd日 HH:mm:ss";
    public final static String TIME_CHINESE_FORMAT_3STR = "yyyy年MM月dd日";
    public final static String TIME_FORMAT_6STR = "yyyy-MM-dd HH:mm:ss";
    public final static String TIME_FORMAT_4STR = "yyyy-MM-dd HH:mm";
    public final static String TIME_FORMAT_3STR = "yyyy-MM-dd";


    /**
     * 文本转日期
     *
     * @param format  日期格式
     * @param strDate 文本
     */
    public static LocalDateTime getDateTimeFromStr(String format, String strDate) {
        // 这里只能转换带时间的格式，不带小时 分钟 这些会报错
        return LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(format));
    }

    /**
     * 文本转日期
     *
     * @param format  日期格式
     * @param strDate 文本
     */
    public static LocalDate getDateFromStr(String strDate, String format) {
        return LocalDate.parse(strDate, DateTimeFormatter.ofPattern(format));
    }


    /**
     * 获取指定格式的日期
     *
     * @param format   格式
     * @param dateTime 时间
     */
    public static String getFormatDate(String format, LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(dateTime);
    }

    /**
     * 获取指定格式的日期
     *
     * @param format 格式
     */
    public static String getFormatDate(String format) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(LocalDateTime.now());
    }

    /**
     * 获取指定格式的日期
     *
     * @param format 格式
     * @param date   时间
     */
    public static String getFormatDate(String format, LocalDate date) {
        if (date == null) {
            return null;
        }
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(format);
        return dtf.format(date);
    }


    /**
     * 获取当前时间
     */
    public static String getFormatDateTime() {
        return getFormatDate(TIME_FORMAT_6STR);
    }


    public static LocalDateTime parse(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
    /**
     * 将秒数转为 HH:mm:ss 格式
     *
     * @param seconds 秒数
     * @return 格式为 HH:mm:ss 的字符串表示
     */
    public static String getFormatSeconds(long seconds) {
        long hours = seconds / 3600;
        long remainingSeconds = seconds % 3600;
        long minutes = remainingSeconds / 60;
        long secs = remainingSeconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

}
