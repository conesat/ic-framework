package cn.icframework.mybatis.processor;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Properties;

/**
 * 属性文件加载器
 * 
 * <p>用于在注解处理器中加载配置文件。支持多种配置文件的查找策略：
 * <ul>
 *   <li>首先尝试从编译输出目录加载</li>
 *   <li>然后尝试从类路径资源加载</li>
 *   <li>最后尝试从项目资源目录加载</li>
 * </ul>
 * 
 * @author IC Framework
 * @since 1.0.0
 */
class Props {

    /** 默认字符编码 */
    private static final String DEFAULT_ENCODING = StandardCharsets.UTF_8.name();
    
    /** 配置文件名称 */
    private static final String CONFIG_FILE_NAME = "ic-mybatis.properties";
    
    /** 存储加载的属性 */
    private final Properties properties;

    /**
     * 构造函数
     * 
     * @param filer 注解处理器的文件创建器，用于访问编译输出目录
     */
    public Props(Filer filer) {
        this.properties = new Properties();
        loadProperties(filer);
    }

    /**
     * 加载属性文件
     * 
     * @param filer 注解处理器的文件创建器
     */
    private void loadProperties(Filer filer) {
        try (InputStream inputStream = findPropertiesInputStream(filer)) {
            if (inputStream != null) {
                properties.load(new InputStreamReader(inputStream, DEFAULT_ENCODING));
            }
        } catch (IOException e) {
            // 在注解处理器中，配置加载失败不应阻止编译过程
            // 因此只记录异常但不抛出
            System.err.println("Failed to load properties file: " + e.getMessage());
        }
    }

    /**
     * 查找属性文件输入流
     * 
     * <p>按以下优先级查找配置文件：
     * <ol>
     *   <li>编译输出目录中的配置文件</li>
     *   <li>类路径中的配置文件</li>
     *   <li>项目资源目录中的配置文件</li>
     * </ol>
     * 
     * @param filer 注解处理器的文件创建器
     * @return 属性文件的输入流，如果未找到则返回null
     * @throws IOException 如果读取文件时发生IO异常
     */
    private InputStream findPropertiesInputStream(Filer filer) throws IOException {
        // 策略1：从编译输出目录加载
        InputStream inputStream = loadFromClassOutput(filer);
        if (inputStream != null) {
            return inputStream;
        }

        // 策略2：从类路径资源加载
        inputStream = loadFromClasspath();
        if (inputStream != null) {
            return inputStream;
        }

        // 策略3：从项目资源目录加载
        return loadFromProjectResources(filer);
    }

    /**
     * 从编译输出目录加载配置文件
     * 
     * @param filer 注解处理器的文件创建器
     * @return 如果文件存在则返回输入流，否则返回null
     * @throws IOException 如果读取文件时发生IO异常
     */
    private InputStream loadFromClassOutput(Filer filer) throws IOException {
        try {
            FileObject propertiesFileObject = filer.getResource(
                StandardLocation.CLASS_OUTPUT, 
                "", 
                CONFIG_FILE_NAME
            );
            
            File propertiesFile = new File(propertiesFileObject.toUri());
            if (propertiesFile.exists()) {
                return propertiesFileObject.openInputStream();
            }
        } catch (IOException e) {
            // 文件不存在或无法访问，继续尝试其他策略
        }
        return null;
    }

    /**
     * 从类路径加载配置文件
     * 
     * @return 如果文件存在则返回输入流，否则返回null
     */
    private InputStream loadFromClasspath() {
        return getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME);
    }

    /**
     * 从项目资源目录加载配置文件
     * 
     * @param filer 注解处理器的文件创建器
     * @return 如果文件存在则返回输入流，否则返回null
     * @throws IOException 如果读取文件时发生IO异常
     */
    private InputStream loadFromProjectResources(Filer filer) throws IOException {
        try {
            FileObject propertiesFileObject = filer.getResource(
                StandardLocation.CLASS_OUTPUT, 
                "", 
                CONFIG_FILE_NAME
            );
            
            File propertiesFile = new File(propertiesFileObject.toUri());
            File pomXmlFile = findPomXmlFile(propertiesFile);
            
            if (pomXmlFile != null && pomXmlFile.exists()) {
                File projectPropertiesFile = new File(
                    pomXmlFile.getParentFile(), 
                    "src/main/resources/" + CONFIG_FILE_NAME
                );
                
                if (projectPropertiesFile.exists()) {
                    return Files.newInputStream(projectPropertiesFile.toPath());
                }
            }
        } catch (IOException e) {
            // 文件不存在或无法访问，返回null
        }
        return null;
    }

    /**
     * 查找pom.xml文件
     * 
     * @param propertiesFile 属性文件
     * @return pom.xml文件，如果未找到则返回null
     */
    private File findPomXmlFile(File propertiesFile) {
        if (propertiesFile == null || !propertiesFile.exists()) {
            return null;
        }
        
        File parent = propertiesFile.getParentFile();
        if (parent == null) {
            return null;
        }
        
        // 向上查找3级目录寻找pom.xml
        for (int i = 0; i < 3; i++) {
            parent = parent.getParentFile();
            if (parent == null) {
                break;
            }
            
            File pomXmlFile = new File(parent, "pom.xml");
            if (pomXmlFile.exists()) {
                return pomXmlFile;
            }
        }
        
        return null;
    }

    /**
     * 获取加载的属性
     * 
     * @return 属性对象，如果未加载到任何属性则返回空属性对象
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * 获取指定键的属性值
     * 
     * @param key 属性键
     * @return 属性值，如果不存在则返回null
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    /**
     * 获取指定键的属性值，如果不存在则返回默认值
     * 
     * @param key 属性键
     * @param defaultValue 默认值
     * @return 属性值或默认值
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }

    /**
     * 检查是否包含指定键
     * 
     * @param key 属性键
     * @return 如果包含该键则返回true
     */
    public boolean containsKey(String key) {
        return properties.containsKey(key);
    }

    /**
     * 获取所有属性键
     * 
     * @return 属性键的集合
     */
    public java.util.Set<String> stringPropertyNames() {
        return properties.stringPropertyNames();
    }
}
