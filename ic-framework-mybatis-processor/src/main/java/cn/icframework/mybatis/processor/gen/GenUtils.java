package cn.icframework.mybatis.processor.gen;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author iceFire
 * @since 2023/6/5
 */
public class GenUtils {
    private static final Pattern TPATTERN = Pattern.compile("[A-Z0-9]");

    /**
     * 驼峰转下划线
     *
     * @param fieldName
     * @return
     */
    public static String luCaseToUnderLine(String fieldName) {
        return luCaseToCharSplit(fieldName, "_");
    }

    public static String luCaseToCharSplit(String fieldName, String chars) {
        if (StringUtils.isBlank(fieldName)) {
            return "";
        }
        fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
        Matcher matcher = TPATTERN.matcher(fieldName);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            matcher.appendReplacement(sb, chars + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

}
