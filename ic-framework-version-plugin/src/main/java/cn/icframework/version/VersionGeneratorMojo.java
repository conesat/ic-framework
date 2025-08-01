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
 * Maven插件：版本信息生成器
 * 在编译时生成version文件，包含版本号和时间戳
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_RESOURCES)
public class VersionGeneratorMojo extends AbstractMojo {
    
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;
    
    @Parameter(property = "version.outputDirectory", defaultValue = "${project.build.directory}/classes")
    private File outputDirectory;
    
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
            
            // 确保输出目录存在
            if (!outputDirectory.exists()) {
                outputDirectory.mkdirs();
            }
            
            // 生成version文件
            File versionFile = new File(outputDirectory, filename);
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