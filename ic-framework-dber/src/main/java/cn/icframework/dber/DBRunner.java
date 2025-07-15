package cn.icframework.dber;

import cn.icframework.core.utils.LocalDateTimeUtils;
import cn.icframework.dber.config.IcSqlConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;


/**
 * 数据库版本管理
 *
 * @author hzl
 * @since 2023/5/13
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class DBRunner implements CommandLineRunner {
    private final SqlSessionFactory sqlSessionFactory;

    private final IcSqlConfig icSqlConfig;

    private final DDLHelper ddlHelper;

    private String lastVPathName = "";
    private String lastVTag = "";
    private final List<Double> vPathList = new ArrayList<>();
    private final List<Double> rPathList = new ArrayList<>();
    private final Map<Double, Path> vPathMap = new HashMap<>();
    private final Map<Double, Path> rPathMap = new HashMap<>();
    private int version = 0;
    private Double versionTagV;
    private boolean init;
    private static final ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();
    /**
     * 用于初始化的脚本必须是这个，可以忽略大小写
     */
    private static final String INIT_SQL = "INIT.SQL";

    @Override
    public void run(String... args) {
        excSqlSource();
        ddlHelper.close();
    }

    /**
     * 执行sql初始化、升级脚本
     */
    private void excSqlSource() {
        try {
            Resource resource = resourceResolver.getResource(icSqlConfig.getUpdatePath());
            if (!resource.exists()) {
                return;
            }
            File sqlPath = resource.getFile();
            if (!sqlPath.isDirectory()) {
                return;
            }
            //记录数据库版本信息
            getVersionInfo();
            //记录数据库版本信息 end

            Set<String> finishInitSqlPath = new HashSet<>();
            if (!init) {
                Resource[] resources = resourceResolver.getResources(icSqlConfig.getInitPath());
                List<File> sysInitSqlFiles = new ArrayList<>();
                for (Resource sysR : resources) {
                    sysInitSqlFiles.add(sysR.getFile());
                }
                if (!sysInitSqlFiles.isEmpty()) {
                    log.info("执行系统模块数据库初始化脚本");
                    for (File sysInitSqlFile : sysInitSqlFiles) {
                        executeSql(sysInitSqlFile.toPath());
                        finishInitSqlPath.add(sysInitSqlFile.getAbsolutePath());
                    }
                    log.info("执行系统模块数据库初始化脚本结束");
                }
            }

            // 获取sql路径配置下面所有的sql
            try (Stream<Path> paths = Files.walk(sqlPath.toPath())) {
                List<Path> pathList = paths.toList();
                for (Path path : pathList) {
                    if (!(Files.isRegularFile(path) && path.getFileName().toString().endsWith(".sql"))) {
                        continue;
                    }
                    if (!init && INIT_SQL.equalsIgnoreCase(path.getFileName().toString()) && !finishInitSqlPath.contains(path.toFile().getAbsolutePath())) {
                        log.info("执行数据库初始化脚本");
                        executeSql(path);
                        log.info("执行数据库初始化脚本结束");
                    } else {
                        // 判断这个文件名
                        // 文件名必须是 Vx.x.x_xxx、Rx.x.xxx 如 V1.0.0_xxx.sql、R1.0.0_xxx.sql
                        String fileName = path.getFileName().toString().toUpperCase();
                        String[] split = fileName.split("_");
                        if (split.length < 1) {
                            continue;
                        }
                        // 取得版本信息 V1.0.0
                        String sql = split[0];
                        if (fileName.startsWith("V")) {
                            // 以V开头的是数据库升级脚本
                            // 取得文件为 1.0.0
                            String v1 = sql.replace("V", "");
                            Double v = getV(v1);
                            // 只取当前版本比数据库大的去执行
                            if (v == null || versionTagV >= v) continue;
                            vPathList.add(v);
                            vPathMap.put(v, path);
                        } else if (fileName.startsWith("R")) {
                            // 以R开头的每次执行更新脚本都会触发，并且按顺序执行
                            // 取得文件为 1.0.0
                            String v1 = sql.replace("R", "");
                            Double v = getV(v1);
                            if (v == null) continue;
                            rPathList.add(v);
                            rPathMap.put(v, path);
                        }
                    }
                }
                Collections.sort(vPathList);
                Collections.sort(rPathList);
                log.info("开始执行数据库升级脚本");
                for (int i = 0; i < vPathList.size(); i++) {
                    Double v = vPathList.get(i);
                    version++;
                    Path path = vPathMap.get(v);
                    executeSql(path);
                    if (i == vPathList.size() - 1) {
                        lastVPathName = path.getFileName().toString();
                        lastVTag = lastVPathName.split("_")[0];
                    }
                }
                for (Double v : rPathList) {
                    executeSql(rPathMap.get(v));
                }
                updateVersionInfo();
                log.info("执行数据库升级脚本结束");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void updateVersionInfo() throws SQLException {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection conn = sqlSession.getConnection();
        if (init && !vPathList.isEmpty()) {
            PreparedStatement preparedStatement = conn.prepareStatement("update sys_db_version set `version` = ?,`version_tag` = ?,`last_update_name` = ?, `update_time` = ?");
            preparedStatement.setInt(1, version);
            preparedStatement.setString(2, lastVTag);
            preparedStatement.setString(3, lastVPathName);
            preparedStatement.setString(4, LocalDateTimeUtils.getFormatDateTime());
            preparedStatement.executeUpdate();
        } else if (!init) {
            PreparedStatement preparedStatement = conn.prepareStatement("insert into sys_db_version  (`version`,`version_tag`,`last_update_name`,`update_time`) value (?,?,?,?)");
            preparedStatement.setInt(1, version);
            preparedStatement.setString(2, StringUtils.hasLength(lastVTag) ? lastVTag : "V0.0.0");
            preparedStatement.setString(3, StringUtils.hasLength(lastVPathName) ? lastVPathName : INIT_SQL);
            preparedStatement.setString(4, LocalDateTimeUtils.getFormatDateTime());
            preparedStatement.executeUpdate();
        }
        sqlSession.flushStatements();
        conn.close();
        sqlSession.close();
    }

    private void getVersionInfo() throws SQLException {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection conn = sqlSession.getConnection();
        Statement statement = conn.createStatement();
        statement.execute("""
                CREATE TABLE IF NOT EXISTS `sys_db_version` (`version` int(4) NOT NULL COMMENT '数据库版本',
                `version_tag` varchar(10) NOT NULL COMMENT '数据库版本标识',
                `last_update_name` varchar(255) COMMENT '最后一次更新的脚本名称',
                `update_time` datetime NOT NULL COMMENT '升级时间') ENGINE=InnoDB DEFAULT CHARSET=utf8;
                """);
        ResultSet resultSet = statement.executeQuery("select * from sys_db_version limit 1");

        if (resultSet.next()) {
            init = true;
            version = resultSet.getInt("version");
            versionTagV = getV(resultSet.getString("version_tag").toUpperCase().replace("V", ""));
        } else {
            versionTagV = 0D;
            init = false;
        }
        sqlSession.flushStatements();
        resultSet.close();
        statement.close();
        conn.close();
        sqlSession.close();
    }

    private void executeSql(Path path) throws SQLException {
        SqlSession sqlSession = sqlSessionFactory.openSession();
        Connection conn = sqlSession.getConnection();
        ScriptRunner scriptRunner = new ScriptRunner(conn);
        scriptRunner.setStopOnError(true);
        try (InputStreamReader inputStreamReader = new InputStreamReader(Files.newInputStream(path.toFile().toPath()), StandardCharsets.UTF_8)) {
            scriptRunner.runScript(inputStreamReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        conn.commit();
        sqlSession.flushStatements();
        conn.close();
        sqlSession.close();
    }

    private static Double getV(String v1) {
        // 取得版本号
        String[] vSplit = v1.split("\\.");
        if (vSplit.length < 1) {
            return 0D;
        }
        double v = 0;
        for (int i = 0; i < vSplit.length; i++) {
            v += Integer.parseInt(vSplit[i]) * Math.pow(10, vSplit.length - 1 - i);
        }
        return v;
    }
}
