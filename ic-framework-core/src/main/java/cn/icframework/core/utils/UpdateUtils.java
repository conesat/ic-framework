package cn.icframework.core.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * 上传工具类
 * @author hzl
 * @since 2021-06-24  16:37:00
 */
@Slf4j
public class UpdateUtils {
    private static final int WEB = 0;
    private static final int JAR = 1;
    private static final String UPDATE_PATH = "/upload/update/";
    private static final String JAR_SH_PATH = "/usr/local/apps/bin/updateJar.sh";
    private static final String WEB_SH_PATH = "/usr/local/apps/bin/updateWeb.sh";

    public synchronized static void update(Integer type, String filePath) {
        try {
            String osName = System.getProperties().getProperty("os.name");
            if (osName.equals("Linux")) {
                if (type == JAR) {
                    updateJar(filePath);
                    return;
                }

                if (type == WEB) {
                    updateWeb(filePath);
                    return;
                }

            } else {
                System.out.println("don't running in Linux");
            }
        }catch (Exception e){
            log.error("更新失败：",e);
        }

    }

    private static void updateJar(String filePath) throws Exception {
        if(!FileUtils.removeFile(filePath, UPDATE_PATH,"mpay.jar")){
            return;
        }
        String command = "chmod 777 " + JAR_SH_PATH ;
        Process process = Runtime.getRuntime().exec(command);
        read(process.getInputStream(),System.out);
        read(process.getErrorStream(),System.err);
        int code = process.waitFor();
        if(code == 0){
            log.info("脚本执行完成");
        }else {
            log.error("脚本执行出错");
        }
    }

    private static void updateWeb(String filePath) throws Exception{
        if(!FileUtils.removeFile(filePath, UPDATE_PATH,"web.tar.gz")){
            return;
        }
        String command = "chmod 777 " + WEB_SH_PATH;
        Process process = Runtime.getRuntime().exec(command);
        read(process.getInputStream(),System.out);
        read(process.getErrorStream(),System.err);
        int code = process.waitFor();
        if(code == 0){
            log.info("脚本执行完成");
        }else {
            log.error("脚本执行出错");
        }
    }

    private static void read(InputStream inputStream, PrintStream out) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();

            }
        }
    }

}
