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
     * @param format
     * @param strDate
     * @return
     */
    public static LocalDateTime getDateTimeFromStr(String format, String strDate) {
        // 这里只能转换带时间的格式，不带小时 分钟 这些会报错
        return LocalDateTime.parse(strDate, DateTimeFormatter.ofPattern(format));
    }

    /**
     * 文本转日期
     *
     * @param format
     * @param strDate
     * @return
     */
    public static LocalDate getDateFromStr(String strDate, String format) {
        return LocalDate.parse(strDate, DateTimeFormatter.ofPattern(format));
    }


    /**
     * 获取指定格式的日期
     *
     * @param format 格式
     * @param dateTime   时间
     * @return
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
     * @return
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
     * @return
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
     *
     * @return
     */
    public static String getFormatDateTime() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(TIME_FORMAT_6STR);
        return dtf.format(LocalDateTime.now());
    }


    public static  LocalDateTime parse(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }
}
