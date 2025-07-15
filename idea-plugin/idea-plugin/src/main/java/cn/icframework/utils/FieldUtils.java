package cn.icframework.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hzl
 * @since 2023/5/18
 */
public class FieldUtils {
    private static final char SPLIT_CHART = '_';
    private static final Pattern TPATTERN = Pattern.compile("[A-Z0-9]");

    /**
     * 驼峰转下划线
     *
     * @param fieldName
     * @return
     */
    public static String luCaseToUnderLine(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return "";
        }
        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
        Matcher matcher = TPATTERN.matcher(fieldName);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 下划线转驼峰
     *
     * @param fieldName
     * @return
     */
    public static String underLineToLUCase(String fieldName) {
        if (StringUtils.isBlank(fieldName)) {
            return "";
        }
        int len = fieldName.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = Character.toLowerCase(fieldName.charAt(i));
            if (c == SPLIT_CHART) {
                if (++i < len) {
                    sb.append(Character.toUpperCase(fieldName.charAt(i)));
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
