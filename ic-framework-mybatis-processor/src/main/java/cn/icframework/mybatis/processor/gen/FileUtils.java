package cn.icframework.mybatis.processor.gen;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class FileUtils {
    public static void writerFile(File file, String content) {
        if (!file.getParentFile().exists() && !file.getParentFile().mkdirs()) {
            return;
        }
        try (PrintWriter writer = new PrintWriter(Files.newOutputStream(file.toPath()), false, StandardCharsets.UTF_8)) {
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void append(File file, String content) {
        try (BufferedWriter out = new BufferedWriter(new FileWriter(file.getAbsoluteFile(), StandardCharsets.UTF_8, true))) {
            out.write(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isAbsolutePath(String path) {
        return path != null && (path.startsWith("/") || path.contains(":"));
    }
}
