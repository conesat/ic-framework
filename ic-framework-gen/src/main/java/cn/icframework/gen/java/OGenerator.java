package cn.icframework.gen.java;

import cn.icframework.mybatis.annotation.Id;
import cn.icframework.mybatis.annotation.TableField;
import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Generator;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;

import static cn.icframework.gen.template.java.OTemplate.*;

/**
 * @author hzl
 * @since 2023/6/5
 */
public class OGenerator {
    private final Generator.Info info;

    public OGenerator(Generator.Info info) {
        this.info = info;
    }

    public void build() {
        Set<String> imports = new HashSet<>();
        String content = getTableFieldsContent(imports);
        String packageName = info.getTableClass() == null ?
                info.getPackageName() + (info.getModuleName() == null ? "" : "." + info.getModuleName()) + "." + info.getModelName().toLowerCase() : info.getPackageName();
        String savePathPrefix = StringUtils.isNotEmpty(info.getPojoJavaPath()) ? info.getPojoJavaPath() : System.getProperty("user.dir") + "/target/gen/";
        String javaPath = savePathPrefix + "/" + packageName.replace(".", "/") + "/pojo";
        javaPath = javaPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
        GenUtils.createFile(javaPath + "/dto", StringUtils.isEmpty(info.getPojoJavaPath()), info.getModelNameFistUp() + "DTO.java", DTO_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#DATE", info.getDate())
                .replaceAll("#CONTENT", content)
                .replaceAll("#IMPORTS", String.join("\n", imports)));
        GenUtils.createFile(javaPath + "/vo", StringUtils.isEmpty(info.getPojoJavaPath()), info.getModelNameFistUp() + "VO.java", VO_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#DATE", info.getDate())
                .replaceAll("#CONTENT", content)
                .replaceAll("#IMPORTS", String.join("\n", imports)));
        GenUtils.createFile(javaPath + "/vo", true, info.getModelNameFistUp() + "VOConverter.java", DTO_CONVERT_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#DATE", info.getDate()));
    }

    /**
     * 遍历全部字段
     * 包括继承的
     *
     * @param imports
     * @return
     */
    private String getTableFieldsContent(Set<String> imports) {
        Class<?> entityClass = info.getTableClass();
        if (entityClass == null) {
            return "";
        }
        StringBuilder contentBuilder = new StringBuilder();
        do {
            Field[] declaredFields = entityClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                TableField tableField = declaredField.getDeclaredAnnotation(TableField.class);
                Id id = declaredField.getDeclaredAnnotation(Id.class);
                if (tableField == null && id == null) {
                    continue;
                }
                if (!declaredField.getType().isPrimitive()) {
                    String[] split = declaredField.getType().getName().split(" ");
                    imports.add("import " + split[split.length - 1] + ";");
                }
                if (tableField != null && StringUtils.isNotEmpty(tableField.comment())) {
                    contentBuilder.append(String.format("""
                                /**
                                 * %s
                                 */
                            """, tableField.comment()));
                }
                if (declaredField.getType().getSimpleName().equals("LocalDateTime")) {
                    imports.add("import com.fasterxml.jackson.annotation.JsonFormat;");
                    contentBuilder.append("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
                }
                contentBuilder.append("    private ")
                        .append(declaredField.getType().getSimpleName())
                        .append(" ")
                        .append(declaredField.getName())
                        .append(";\n");
            }
            entityClass = entityClass.getSuperclass();
        } while (entityClass != null);
        return contentBuilder.toString();
    }
}
