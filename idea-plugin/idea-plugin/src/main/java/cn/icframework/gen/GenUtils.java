package cn.icframework.gen;

import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
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

    public static void createFile(String path, boolean over, String fileName, String content) {
        File file = Path.of(path, fileName).toFile();
        if (!over && file.exists()){
            return;
        }
        writeFile(file, content);
    }
    public static void writeFile(File file, String content) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.write(content);
            writer.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String readFile(String filePath) {
        if (Files.notExists(Path.of(filePath))) {
            return "";
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
