package cn.icframework.mybatis.processor;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.Table;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.mybatis.processor.gen.java.DefGenerator;
import cn.icframework.mybatis.query.QueryField;
import cn.icframework.mybatis.utils.ModelClassUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 表注解处理器
 *
 * <p>该处理器用于处理标记了 {@link Table} 注解的实体类，自动生成对应的查询字段定义类。
 * 主要功能包括：
 * <ul>
 *   <li>扫描标记了 {@link Table} 注解的实体类</li>
 *   <li>解析实体类中的字段信息，包括 {@link TableField} 和 {@link Id} 注解</li>
 *   <li>生成对应的查询字段定义类（Def类）</li>
 *   <li>支持继承关系的字段解析</li>
 * </ul>
 *
 * <p>生成的Def类包含：
 * <ul>
 *   <li>对应实体类中所有标记了 {@link TableField} 或 {@link Id} 注解的字段</li>
 *   <li>每个字段对应一个 {@link QueryField} 实例</li>
 *   <li>支持链式查询操作</li>
 * </ul>
 *
 * @author IC Framework
 * @see Table
 * @see TableField
 * @see Id
 * @see Generator
 * @since 1.0.0
 */
public class TableProcessor extends AbstractProcessor {

    /**
     * 配置属性键：代码生成路径
     */
    private static final String CONFIG_KEY_GEN_PATH = "processor.gen-path";

    /**
     * 默认代码生成路径
     */
    private static final String DEFAULT_GEN_PATH = "";

    /**
     * 日期格式化器
     */
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");


    /**
     * 日志颜色：蓝色（信息）
     */
    private static final String COLOR_BLUE = "\033[0;34m";

    /**
     * 日志颜色：绿色（成功）
     */
    private static final String COLOR_GREEN = "\033[0;32m";

    /**
     * 日志颜色：红色（错误）
     */
    private static final String COLOR_RED = "\033[0;31m";

    /**
     * 日志颜色：重置
     */
    private static final String COLOR_RESET = "\033[0;29m";

    /**
     * 配置属性管理器
     */
    private Props props;

    /**
     * 文件创建器
     */
    private Filer filer;

    /**
     * 类型工具
     */
    private Types typeUtils;

    /**
     * 初始化处理器
     *
     * <p>在处理器开始工作前调用，用于初始化必要的工具和配置。
     *
     * @param processingEnv 处理环境，提供各种工具和配置
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.filer = processingEnv.getFiler();
        this.typeUtils = processingEnv.getTypeUtils();
        this.props = new Props(filer);
    }

    /**
     * 获取支持的注解类型
     *
     * @return 支持的注解类型集合，目前只支持 {@link Table} 注解
     */
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> supportedAnnotationTypes = new HashSet<>();
        supportedAnnotationTypes.add(Table.class.getCanonicalName());
        return supportedAnnotationTypes;
    }

    /**
     * 获取支持的源代码版本
     *
     * @return 支持的最新源代码版本
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    /**
     * 处理注解
     *
     * <p>这是处理器的核心方法，负责：
     * <ul>
     *   <li>扫描所有标记了 {@link Table} 注解的类</li>
     *   <li>解析类的字段信息</li>
     *   <li>生成对应的Def类</li>
     *   <li>输出处理日志</li>
     * </ul>
     *
     * @param annotations 当前轮次中要处理的注解类型集合
     * @param roundEnv    当前轮次的环境信息
     * @return 如果返回true，表示这些注解已经被此处理器声明，其他处理器不应再处理它们
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        // 只在非最后一轮处理中执行，避免重复处理
        if (roundEnv.processingOver()) {
            return false;
        }
        try {
            // 读取配置
            String javaGenPath = getGenPath();

            // 处理所有标记了 @Table 注解的元素
            for (Element entityClassElement : roundEnv.getElementsAnnotatedWith(Table.class)) {
                processEntityClass(entityClassElement, javaGenPath);
            }
        } catch (Exception e) {
            logError("处理注解时发生错误: " + e.getMessage(), e);
        }

        return false;
    }

    /**
     * 获取代码生成路径
     *
     * @return 配置的代码生成路径，如果未配置则返回默认值
     */
    private String getGenPath() {
        return props.getProperties().getProperty(CONFIG_KEY_GEN_PATH, DEFAULT_GEN_PATH);
    }

    /**
     * 处理实体类
     *
     * <p>对单个实体类进行处理，包括：
     * <ul>
     *   <li>验证元素类型</li>
     *   <li>检查生成配置</li>
     *   <li>解析类信息</li>
     *   <li>生成Def类</li>
     *   <li>输出处理日志</li>
     * </ul>
     *
     * @param entityClassElement 实体类元素
     * @param javaGenPath        代码生成路径
     */
    private void processEntityClass(Element entityClassElement, String javaGenPath) {
        // 验证元素类型
        if (entityClassElement.getKind() != ElementKind.CLASS) {
            return;
        }

        // 检查生成配置
        if (!shouldGenerate(entityClassElement)) {
            return;
        }

        // 解析类信息
        ClassInfo classInfo = parseClassInfo(entityClassElement);

        // 解析字段信息
        String defContent = parseFields(entityClassElement, classInfo);

        // 生成Def类
        generateDefClass(entityClassElement, classInfo, defContent, javaGenPath);

        // 输出成功日志
        logSuccess("ic mybatis gen :: " + entityClassElement);
    }

    /**
     * 检查是否应该生成代码
     *
     * @param entityClassElement 实体类元素
     * @return 如果应该生成则返回true，否则返回false
     */
    private boolean shouldGenerate(Element entityClassElement) {
        Generator generator = entityClassElement.getAnnotation(Generator.class);
        return generator == null || generator.gen();
    }

    /**
     * 解析类信息
     *
     * @param entityClassElement 实体类元素
     * @return 包含类信息的对象
     */
    private ClassInfo parseClassInfo(Element entityClassElement) {
        String fullClassName = entityClassElement.toString();
        String packageName = fullClassName.substring(0, fullClassName.lastIndexOf("."));
        String modelName = entityClassElement.getSimpleName().toString();

        // 生成首字母大写的类名（去掉第一个字符后首字母大写）
        String firstChar = modelName.substring(0, 1);
        String remainingChars = modelName.substring(1);
        String modelNameFirstUp = firstChar.toUpperCase() + remainingChars;

        // 生成首字母小写的类名
        String modelNameFirstDown = firstChar.toLowerCase() + remainingChars;

        // 生成当前日期
        String currentDate = DATE_FORMATTER.format(LocalDateTime.now());

        return new ClassInfo(packageName, modelName, modelNameFirstUp, modelNameFirstDown, currentDate);
    }

    /**
     * 解析字段信息
     *
     * <p>递归解析类的所有字段，包括继承的字段，生成对应的查询字段定义。
     *
     * @param entityClassElement 实体类元素
     * @param classInfo          类信息
     * @return 生成的字段定义内容
     */
    private String parseFields(Element entityClassElement, ClassInfo classInfo) {
        StringBuilder defContentBuilder = new StringBuilder();
        Set<String> processedFields = new HashSet<>();
        TypeElement classElement = (TypeElement) entityClassElement;
        // 递归处理类的继承层次
        do {
            Table table = classElement.getAnnotation(Table.class);
            for (Element enclosedElement : classElement.getEnclosedElements()) {
                if (enclosedElement.getKind() == ElementKind.FIELD) {
                    processField(table, enclosedElement, classInfo, defContentBuilder, processedFields);
                }
            }

            // 处理父类
            TypeMirror superclass = classElement.getSuperclass();
            if (Objects.equals(superclass.toString(), Object.class.getName())) {
                break;
            }
            classElement = (TypeElement) typeUtils.asElement(superclass);
        } while (classElement != null);

        return defContentBuilder.toString();
    }

    /**
     * 处理单个字段
     *
     * @param fieldElement      字段元素
     * @param classInfo         类信息
     * @param defContentBuilder 定义内容构建器
     * @param processedFields   已处理字段集合，用于去重
     */
    private void processField(Table table, Element fieldElement, ClassInfo classInfo,
                              StringBuilder defContentBuilder, Set<String> processedFields) {
        // 跳过静态字段
        Set<Modifier> modifiers = fieldElement.getModifiers();
        if (modifiers.contains(Modifier.STATIC)) {
            return;
        }

        String fieldName = fieldElement.getSimpleName().toString();

        // 检查字段是否已处理过（避免重复）
        if (processedFields.contains(fieldName)) {
            return;
        }

        // 检查字段是否有相关注解
        TableField tableField = fieldElement.getAnnotation(TableField.class);
        Id id = fieldElement.getAnnotation(Id.class);

        if (id == null && tableField == null) {
            return;
        }

        // 标记字段已处理
        processedFields.add(fieldName);

        // 生成字段定义
        String columnName = ModelClassUtils.getColumnName(table, tableField, fieldName);
        String fieldDefinition = String.format(
                "    public QueryField<%sDef> %s = new QueryField<>(this, \"%s\");\n",
                classInfo.modelNameFirstUp(), fieldName, columnName
        );

        defContentBuilder.append(fieldDefinition);
    }


    /**
     * 生成Def类
     *
     * @param entityClassElement 实体类元素
     * @param classInfo          类信息
     * @param defContent         字段定义内容
     * @param javaGenPath        代码生成路径
     */
    private void generateDefClass(Element entityClassElement, ClassInfo classInfo,
                                  String defContent, String javaGenPath) {
        DefGenerator.genFile(
                filer,
                javaGenPath,
                classInfo.packageName(),
                classInfo.modelName(),
                classInfo.modelNameFirstUp(),
                classInfo.modelNameFirstDown(),
                classInfo.currentDate(),
                defContent,
                entityClassElement
        );
    }

    /**
     * 输出成功日志
     *
     * @param message 日志消息
     */
    private void logSuccess(String message) {
        System.out.printf("%-1s", "[");
        System.out.format(COLOR_BLUE);
        System.out.printf("%-4s", "INFO");
        System.out.format(COLOR_RESET);
        System.out.printf("%-2s", "]");
        System.out.printf("%-26s", "--- " + message);
        System.out.format(COLOR_GREEN);
        System.out.printf("%-7s", "\tSUCCESS");
        System.out.format(COLOR_RESET);
        System.out.println();
    }

    /**
     * 输出错误日志
     *
     * @param message   错误消息
     * @param throwable 异常对象
     */
    private void logError(String message, Throwable throwable) {
        System.out.printf("%-1s", "[");
        System.out.format(COLOR_RED);
        System.out.printf("%-4s", "ERROR");
        System.out.format(COLOR_RESET);
        System.out.printf("%-2s", "]");
        System.out.printf("%-26s", "--- " + message);
        System.out.println();

        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    /**
     * 类信息封装类
     *
     * <p>用于封装实体类的相关信息，便于在方法间传递。
     *
     * @author IC Framework
     * @since 1.0.0
     */
    private record ClassInfo(String packageName, String modelName, String modelNameFirstUp, String modelNameFirstDown,
                             String currentDate) {
    }
}
