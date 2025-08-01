package cn.icframework.version;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Maven插件：在resources目录生成版本信息文件
 * 在编译时生成version文件到src/main/resources目录
 */
@Mojo(name = "resources", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class VersionResourcesMojo extends AbstractMojo {
    
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    
    @Parameter(property = "version.filename", defaultValue = "version")
    private String filename;
    
    @Parameter(property = "version.timestampFormat", defaultValue = "yyyyMMddHHmmss")
    private String timestampFormat;
    
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            // 获取项目版本号
            String version = project.getVersion();
            
            // 生成时间戳
            SimpleDateFormat sdf = new SimpleDateFormat(timestampFormat);
            String timestamp = sdf.format(new Date());
            
            // 创建JSON对象
            JSONObject versionInfo = new JSONObject();
            versionInfo.put("version", version);
            versionInfo.put("timestamp", timestamp);
            
            // 获取resources目录
            File resourcesDir = new File(project.getBasedir(), "src/main/resources");
            if (!resourcesDir.exists()) {
                resourcesDir.mkdirs();
            }
            
            // 生成version文件
            File versionFile = new File(resourcesDir, filename);
            try (FileWriter writer = new FileWriter(versionFile)) {
                writer.write(JSON.toJSONString(versionInfo, true));
            }
            
            getLog().info("Version file generated successfully: " + versionFile.getAbsolutePath());
            getLog().info("Version: " + version);
            getLog().info("Timestamp: " + timestamp);
            
        } catch (IOException e) {
            throw new MojoExecutionException("Error generating version file", e);
        }
    }
} 