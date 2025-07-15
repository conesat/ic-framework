package cn.icframework.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Scanner;

/**
 * 文件工具类，提供文件相关的常用操作方法。
 * <p>
 * 所有方法均为静态方法。
 * </p>
 * @author hzl
 * @since 2021-06-24  17:44:00
 */
@Slf4j
public class FileUtils {
    /**
     * 移动文件到指定目录并重命名。
     * @param fileName 源文件路径
     * @param destPath 目标文件夹路径
     * @param destName 目标文件名
     * @return 移动成功返回 true，否则返回 false
     */
    public static boolean removeFile(String fileName, String destPath, String destName) {
        File file = new File(fileName);
        File destDir = new File(destPath);
        //检查目标路径是否合法
        if (destDir.exists()) {
            if (destDir.isFile()) {
                log.error("目标路径是个文件，请检查目标路径！");
                return false;
            }
        } else {
            if (!destDir.mkdirs()) {
                log.error("目标文件夹不存在，创建失败！");
                return false;
            }
        }
        //检查源文件是否合法
        if (file.isFile() && file.exists()) {
            String destinationFile = destPath + "/" + destName;
            if (!file.renameTo(new File(destinationFile))) {
                log.error("移动文件失败！");
                return false;
            }
        } else {
            log.error("要备份的文件路径不正确，移动失败！");
            return false;
        }
        log.info("已成功移动文件" + file.getName() + "到" + destPath);
        return true;
    }

    /**
     * 读取文件内容，默认不换行。
     * @param file 文件对象
     * @return 文件内容字符串
     */
    public static String readFile(File file) {
        return readFile(file, false);
    }

    /**
     * 读取文件内容，可选择是否换行。
     * @param file 文件对象
     * @param h 是否换行
     * @return 文件内容字符串
     */
    public static String readFile(File file, boolean h) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            StringBuilder sb = new StringBuilder();
            while ((st = br.readLine()) != null) {
                sb.append(st);
                if (h) {
                    sb.append("\r\n");
                }
            }
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件大小的可读文本描述。
     * @param size 文件大小（字节）
     * @return 文件大小描述字符串
     */
    public static String getNetFileSizeDescription(long size) {
        StringBuilder bytes = new StringBuilder();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("GB");
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("MB");
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }

    /**
     * 从输入流中读取字符串内容。
     * @param is 输入流
     * @return 输入流内容字符串
     */
    public static String readStream(InputStream is) {
        try (Scanner scanner = new Scanner(is, StandardCharsets.UTF_8)) {
            scanner.useDelimiter("\\A"); // 一次性读取整个流
            return scanner.hasNext() ? scanner.next() : "";
        }
    }
}
