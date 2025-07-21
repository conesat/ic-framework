package cn.icframework.gen.java;


import cn.icframework.gen.GenUtils;
import cn.icframework.gen.Info;
import cn.icframework.gen.TableField;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static cn.icframework.temp.java.DefTemplate.DEF_TEMPLATE;


/**
 * @author hzl
 * @since 2023/6/5
 */
public class DefGenerator {


    public static void build(Info info) {
        String packageName = info.getPackageName();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");

        StringBuilder defContentStringBuilder = new StringBuilder();
        Set<String> columnsSet = new HashSet<>();
        // 获取字段
        for (TableField field : info.getTableInfo().getFields()) {
            String fieldName = field.getName();
            if (columnsSet.contains(fieldName)) {
                continue;
            }
            columnsSet.add(fieldName);
            defContentStringBuilder.append("    public QueryField<").append(info.getModelNameFistUp()).append("Def> ")
                    .append(fieldName).append("= new QueryField<>(this, \"").append(field.getTableColumnName()).append("\");\n");
        }

        GenUtils.createFile(info.getTableDefPath() + "/" + packageName.replace(".", "/") + "/def",
                true,
                info.getModelNameFistUp() + "Def.java",
                buildContent(packageName, info.getModelName(), info.getModelNameFistUp(), info.getModelNameFistDown(), dtf.format(LocalDateTime.now()),
                        defContentStringBuilder.toString()));
    }

    private static String buildContent(String packageName,
                                       String modelName,
                                       String modelNameFistUp,
                                       String modelNameFistDown,
                                       String date,
                                       String content) {
        String luCaseToCharSplit = GenUtils.luCaseToCharSplit(modelName, "-");
        return DEF_TEMPLATE
                .replaceAll("#PACKAGE_NAME", packageName)
                .replaceAll("#MODEL_NAME_FIST_UP", modelNameFistUp)
                .replaceAll("#MODEL_NAME_FIST_DOWN", modelNameFistDown)
                .replaceAll("#AUTHOR", "ic generator")
                .replaceAll("#CONTENT", content)
                .replaceAll("#LU_CASE_TO_CHAR_SPLIT", luCaseToCharSplit)
                .replaceAll("#DATE", date);
    }
}
