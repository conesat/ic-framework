package cn.icframework.core;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * IC Framework 启动器。
 * <p>
 * 在应用启动时显示框架横幅。
 * </p>
 * @author hzl
 * @since 2023/5/27 0027
 */
@Component
@Order(1)
public class IcFrameworkStarter implements CommandLineRunner {

    /**
     * 默认构造函数。
     */
    public IcFrameworkStarter() {
        // 默认构造函数
    }

    /**
     * 应用启动时执行，打印框架横幅。
     * @param args 启动参数
     */
    @Override
    public void run(String... args) {
        System.out.format("\033[0;34m");
        System.out.println("""
                \s
                  _____ _____   ______                                           _   \s
                 |_   _/ ____| |  ____|                                         | |  \s
                   | || |      | |__ _ __ __ _ _ __ ___   _____      _____  _ __| | __
                   | || |      |  __| '__/ _` | '_ ` _ \\ / _ \\ \\ /\\ / / _ \\| '__| |/ /
                  _| || |____  | |  | | | (_| | | | | | |  __/\\ V  V / (_) | |  |   <\s
                 |_____\\_____| |_|  |_|  \\__,_|_| |_| |_|\\___| \\_/\\_/ \\___/|_|  |_|\\_\\
                 =============================================================================                       \s
                 << IC Framework V1.0.0 -- start successful >>                                                              \s
                """);
        System.out.format("\033[0;29m");
    }
}
