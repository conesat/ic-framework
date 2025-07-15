package cn.icframework.gen;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author iceFire
 * @since 2023/6/5
 */
@Slf4j
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

    public static void createFile(String path, boolean over, String fileName, String content) {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        File file1 = Path.of(path, fileName).toFile();
        if (!over && file1.exists()){
            log.error("文件：" + file1.getAbsoluteFile() + "已存在，跳过");
            return;
        }
        log.info("创建文件：" + file1.getAbsoluteFile());
        try (PrintWriter writer = new PrintWriter(file1)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
