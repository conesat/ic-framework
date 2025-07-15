package cn.icframework.gen.java;

import cn.icframework.gen.*;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

import static cn.icframework.temp.java.OTemplate.*;


/**
 * @author hzl
 * @since 2023/6/5
 */
public class OGenerator {
    private final Info info;

    public OGenerator(Info info) {
        this.info = info;
    }

    public void build() {
        Set<String> imports = new HashSet<>();
        String content = getTableFieldsContent(imports);
        String packageName = info.getTableInfo() == null ?
                info.getPackageName() + (info.getModuleName() == null ? "" : "." + info.getModuleName()) + "." + info.getModelName().toLowerCase() : info.getPackageName();
        String savePathPrefix = StringUtils.isNotEmpty(info.getPojoJavaPath()) ? info.getPojoJavaPath() : System.getProperty("user.dir") + "/target/gen/";
        String javaPath = savePathPrefix + "/" + packageName.replace(".", "/") + "/pojo";
        javaPath = javaPath.replaceAll("/", Matcher.quoteReplacement(File.separator));
        GenUtils.createFile(javaPath + "/dto", info.getOver(), info.getModelNameFistUp() + "DTO.java", DTO_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#DATE", info.getDate())
                .replaceAll("#CONTENT", content)
                .replaceAll("#IMPORTS", String.join("\n", imports)));
        GenUtils.createFile(javaPath + "/vo", info.getOver(), info.getModelNameFistUp() + "VO.java", VO_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", info.getModelNameFistUp())
                .replaceAll("#MODEL_NAME_FIST_DOWN", info.getModelNameFistDown())
                .replaceAll("#AUTHOR", info.getAuthor())
                .replaceAll("#DATE", info.getDate())
                .replaceAll("#CONTENT", content)
                .replaceAll("#IMPORTS", String.join("\n", imports)));
        GenUtils.createFile(javaPath + "/vo", info.getOver(), info.getModelNameFistUp() + "VOConverter.java", DTO_CONVERT_TEMPLATE
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
        TableInfo tableInfo = info.getTableInfo();
        if (tableInfo == null) {
            return "";
        }
        StringBuilder contentBuilder = new StringBuilder();
        List<TableField> fields = tableInfo.getFields();
        for (TableField field : fields) {
            if (!field.isPrimitive()) {
                imports.add("import " + field.getTypeName() + ";");
            }
            if (StringUtils.isNotEmpty(field.getComment())) {
                contentBuilder.append(String.format("""
                                /**
                                 * %s
                                 */
                            """, field.getComment()));
            }
            if (field.getTypeSimpleName().equals("LocalDateTime")) {
                imports.add("import com.fasterxml.jackson.annotation.JsonFormat;");
                contentBuilder.append("    @JsonFormat(pattern = \"yyyy-MM-dd HH:mm:ss\")\n");
            }
            contentBuilder.append("    private ")
                    .append(field.getTypeSimpleName())
                    .append(" ")
                    .append(field.getName())
                    .append(";\n");
        }
        return contentBuilder.toString();
    }
}
